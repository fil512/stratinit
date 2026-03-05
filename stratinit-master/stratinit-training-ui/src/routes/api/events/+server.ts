import { createClient } from 'redis';
import type { RequestHandler } from '@sveltejs/kit';

export const GET: RequestHandler = async () => {
	const redisHost = process.env['REDIS_HOST'] || 'localhost';
	const redisPort = parseInt(process.env['REDIS_PORT'] || '6379');

	const stream = new ReadableStream({
		async start(controller) {
			const subscriber = createClient({
				url: `redis://${redisHost}:${redisPort}`
			});

			subscriber.on('error', (err: Error) => {
				console.error('Redis subscriber error:', err.message);
				const data = JSON.stringify({ type: 'error', message: 'Redis connection lost' });
				controller.enqueue(`data: ${data}\n\n`);
			});

			try {
				await subscriber.connect();

				// Send connection status
				controller.enqueue(`data: ${JSON.stringify({ type: 'connected' })}\n\n`);

				// Subscribe to all training channels
				const channels = [
					'training:tick',
					'training:game-result',
					'training:generation',
					'training:session'
				];

				for (const channel of channels) {
					await subscriber.subscribe(channel, (message) => {
						try {
							controller.enqueue(`data: ${message}\n\n`);
						} catch {
							// Stream closed
						}
					});
				}
			} catch (err) {
				console.error('Failed to connect to Redis:', err);
				const data = JSON.stringify({
					type: 'error',
					message: 'Cannot connect to Redis. Is it running?'
				});
				controller.enqueue(`data: ${data}\n\n`);
			}

			// Send heartbeat every 30s to keep connection alive
			const heartbeat = setInterval(() => {
				try {
					controller.enqueue(`: heartbeat\n\n`);
				} catch {
					clearInterval(heartbeat);
				}
			}, 30000);
		}
	});

	return new Response(stream, {
		headers: {
			'Content-Type': 'text/event-stream',
			'Cache-Control': 'no-cache',
			Connection: 'keep-alive'
		}
	});
};
