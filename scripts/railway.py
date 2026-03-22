#!/usr/bin/env python3
"""
Railway API client for the stratinit project.

Usage:
    python scripts/railway.py setup               # Create Railway project + configure everything (run once)
    python scripts/railway.py status              # Show deployment status
    python scripts/railway.py logs [-n 50]        # Show deployment logs
    python scripts/railway.py deployments         # List recent deployments
    python scripts/railway.py setvar KEY VALUE    # Set environment variable
    python scripts/railway.py getvar [KEY]        # Get environment variable(s)
    python scripts/railway.py redeploy            # Trigger a new deployment
    python scripts/railway.py health              # Check health endpoint
    python scripts/railway.py projects            # List all projects
    python scripts/railway.py raw 'query {...}'   # Execute raw GraphQL query
"""

import os
import sys
import json
import argparse
import urllib.request
import urllib.error

RAILWAY_API_URL = "https://backboard.railway.com/graphql/v2"

# stratinit project constants (from Railway dashboard)
# Run 'python scripts/railway.py projects' to populate these after project creation
PROJECT_ID = ""
ENVIRONMENT_ID = ""  # production

# Service IDs
SERVICES = {
    "server": "",  # Spring Boot backend service ID
}

# Public URL (fill in after running 'domain create')
PUBLIC_URL = ""

# Active service (set by --service flag)
_active_service = "server"


def get_service_id():
    return SERVICES[_active_service]


def get_token():
    """Get Railway API token from environment."""
    token = os.environ.get("RAILWAY_TOKEN")
    if not token:
        print("Error: RAILWAY_TOKEN environment variable not set", file=sys.stderr)
        print("Create a token at: https://railway.com/account/tokens", file=sys.stderr)
        sys.exit(1)
    return token


def graphql_query(query, variables=None):
    """Execute a GraphQL query against the Railway API using Bearer auth."""
    token = get_token()

    payload = {"query": query}
    if variables:
        payload["variables"] = variables

    data = json.dumps(payload).encode("utf-8")

    req = urllib.request.Request(
        RAILWAY_API_URL,
        data=data,
        headers={
            "Content-Type": "application/json",
            "User-Agent": "stratinit-railway-client/1.0",
            "Accept": "application/json",
            "Authorization": f"Bearer {token}",
        },
        method="POST"
    )

    try:
        with urllib.request.urlopen(req) as response:
            result = json.loads(response.read().decode("utf-8"))
            if "errors" in result:
                for error in result["errors"]:
                    print(f"GraphQL Error: {error['message']}", file=sys.stderr)
                if not result.get("data"):
                    sys.exit(1)
            return result.get("data")
    except urllib.error.HTTPError as e:
        print(f"HTTP Error {e.code}: {e.reason}", file=sys.stderr)
        try:
            error_body = e.read().decode("utf-8")
            try:
                error_json = json.loads(error_body)
                print(json.dumps(error_json, indent=2), file=sys.stderr)
            except json.JSONDecodeError:
                print(error_body, file=sys.stderr)
        except Exception:
            pass
        sys.exit(1)


def get_deployments(limit=5):
    """Get recent deployments for the active service."""
    query = """
    query ListDeployments($serviceId: String!, $environmentId: String!, $limit: Int!) {
        deployments(first: $limit, input: {serviceId: $serviceId, environmentId: $environmentId}) {
            edges {
                node {
                    id
                    status
                    createdAt
                }
            }
        }
    }
    """
    return graphql_query(query, {
        "serviceId": get_service_id(),
        "environmentId": ENVIRONMENT_ID,
        "limit": limit
    })


def get_deployment_logs(deployment_id, limit=100):
    """Get runtime logs for a specific deployment."""
    query = """
    query DeploymentLogs($deploymentId: String!, $limit: Int) {
        deploymentLogs(deploymentId: $deploymentId, limit: $limit) {
            timestamp
            message
            severity
        }
    }
    """
    return graphql_query(query, {"deploymentId": deployment_id, "limit": limit})


def get_build_logs(deployment_id, limit=100):
    """Get build logs for a specific deployment."""
    query = """
    query BuildLogs($deploymentId: String!, $limit: Int) {
        buildLogs(deploymentId: $deploymentId, limit: $limit) {
            timestamp
            message
            severity
        }
    }
    """
    return graphql_query(query, {"deploymentId": deployment_id, "limit": limit})


def get_variables():
    """Get all variables for the active service."""
    query = """
    query GetVariables($projectId: String!, $environmentId: String!, $serviceId: String!) {
        variablesForServiceDeployment(
            projectId: $projectId,
            environmentId: $environmentId,
            serviceId: $serviceId
        )
    }
    """
    return graphql_query(query, {
        "projectId": PROJECT_ID,
        "environmentId": ENVIRONMENT_ID,
        "serviceId": get_service_id()
    })


def set_variable(name, value):
    """Set an environment variable for the active service."""
    query = """
    mutation SetVariable($input: VariableUpsertInput!) {
        variableUpsert(input: $input)
    }
    """
    return graphql_query(query, {
        "input": {
            "projectId": PROJECT_ID,
            "environmentId": ENVIRONMENT_ID,
            "serviceId": get_service_id(),
            "name": name,
            "value": value
        }
    })


def delete_variable(name):
    """Delete an environment variable."""
    query = """
    mutation DeleteVariable($input: VariableDeleteInput!) {
        variableDelete(input: $input)
    }
    """
    return graphql_query(query, {
        "input": {
            "projectId": PROJECT_ID,
            "environmentId": ENVIRONMENT_ID,
            "serviceId": get_service_id(),
            "name": name
        }
    })


def update_service_instance(updates):
    """Update service instance settings (startCommand, buildCommand, healthcheckPath, etc.)."""
    query = """
    mutation UpdateServiceInstance($serviceId: String!, $environmentId: String!, $input: ServiceInstanceUpdateInput!) {
        serviceInstanceUpdate(serviceId: $serviceId, environmentId: $environmentId, input: $input)
    }
    """
    return graphql_query(query, {
        "serviceId": get_service_id(),
        "environmentId": ENVIRONMENT_ID,
        "input": updates
    })


def create_domain():
    """Generate a Railway subdomain for the active service."""
    query = """
    mutation CreateDomain($serviceId: String!, $environmentId: String!) {
        serviceDomainCreate(input: {serviceId: $serviceId, environmentId: $environmentId}) {
            domain
        }
    }
    """
    return graphql_query(query, {
        "serviceId": get_service_id(),
        "environmentId": ENVIRONMENT_ID,
    })


def get_domains():
    """Get domains for the active service."""
    query = """
    query GetDomains($projectId: String!, $serviceId: String!, $environmentId: String!) {
        domains(projectId: $projectId, serviceId: $serviceId, environmentId: $environmentId) {
            serviceDomains { domain }
            customDomains { domain }
        }
    }
    """
    return graphql_query(query, {
        "projectId": PROJECT_ID,
        "serviceId": get_service_id(),
        "environmentId": ENVIRONMENT_ID,
    })


def add_postgres_plugin():
    """Add a PostgreSQL database to the project via a database service."""
    query = """
    mutation AddPostgres($projectId: String!, $environmentId: String!) {
        templateDeploy(input: {
            projectId: $projectId,
            environmentId: $environmentId,
            services: [{
                name: "Postgres",
                source: { image: "ghcr.io/railwayapp-templates/postgres-ssl:16" }
            }],
            templateCode: "postgres"
        }) {
            projectId
            workflowId
        }
    }
    """
    return graphql_query(query, {
        "projectId": PROJECT_ID,
        "environmentId": ENVIRONMENT_ID,
    })


def trigger_deployment():
    """Trigger a new deployment for the active service."""
    query = """
    mutation TriggerDeploy($input: EnvironmentTriggersDeployInput!) {
        environmentTriggersDeploy(input: $input)
    }
    """
    return graphql_query(query, {
        "input": {
            "environmentId": ENVIRONMENT_ID,
            "projectId": PROJECT_ID,
            "serviceId": get_service_id()
        }
    })


def create_project(name):
    """Create a new Railway project."""
    query = """
    mutation CreateProject($input: ProjectCreateInput!) {
        projectCreate(input: $input) {
            id
            environments { edges { node { id name } } }
        }
    }
    """
    return graphql_query(query, {"input": {"name": name}})


def create_service(project_id, name, repo=None, branch="master"):
    """Create a service in a project, optionally connected to a GitHub repo."""
    source = {}
    if repo:
        source = {"repo": repo, "branch": branch}
    query = """
    mutation CreateService($input: ServiceCreateInput!) {
        serviceCreate(input: $input) {
            id
            name
        }
    }
    """
    inp = {"name": name, "projectId": project_id}
    if source:
        inp["source"] = source
    return graphql_query(query, {"input": inp})


def set_variable_explicit(project_id, environment_id, service_id, name, value):
    """Set an environment variable with explicit IDs (for use during setup)."""
    query = """
    mutation SetVariable($input: VariableUpsertInput!) {
        variableUpsert(input: $input)
    }
    """
    return graphql_query(query, {
        "input": {
            "projectId": project_id,
            "environmentId": environment_id,
            "serviceId": service_id,
            "name": name,
            "value": value
        }
    })


def create_domain_explicit(service_id, environment_id):
    """Generate a Railway subdomain with explicit IDs."""
    query = """
    mutation CreateDomain($serviceId: String!, $environmentId: String!) {
        serviceDomainCreate(input: {serviceId: $serviceId, environmentId: $environmentId}) {
            domain
        }
    }
    """
    return graphql_query(query, {
        "serviceId": service_id,
        "environmentId": environment_id,
    })


def trigger_deployment_explicit(project_id, environment_id, service_id):
    """Trigger a deployment with explicit IDs."""
    query = """
    mutation TriggerDeploy($input: EnvironmentTriggersDeployInput!) {
        environmentTriggersDeploy(input: $input)
    }
    """
    return graphql_query(query, {
        "input": {
            "environmentId": environment_id,
            "projectId": project_id,
            "serviceId": service_id
        }
    })


def add_postgres_explicit(project_id, environment_id):
    """Add PostgreSQL to a project with explicit IDs."""
    query = """
    mutation AddPostgres($projectId: String!, $environmentId: String!) {
        templateDeploy(input: {
            projectId: $projectId,
            environmentId: $environmentId,
            services: [{
                name: "Postgres",
                source: { image: "ghcr.io/railwayapp-templates/postgres-ssl:16" }
            }],
            templateCode: "postgres"
        }) {
            projectId
            workflowId
        }
    }
    """
    return graphql_query(query, {
        "projectId": project_id,
        "environmentId": environment_id,
    })


# ============ Command Handlers ============

def cmd_status(args):
    """Show current deployment status."""
    data = get_deployments(1)
    if not data:
        print("No data returned")
        return

    edges = data.get("deployments", {}).get("edges", [])
    if not edges:
        print("No deployments found")
        return

    dep = edges[0]["node"]
    status = dep["status"]
    created = dep["createdAt"][:19].replace("T", " ")

    status_icon = {
        "SUCCESS": "✓",
        "FAILED": "✗",
        "BUILDING": "⟳",
        "DEPLOYING": "⟳",
        "INITIALIZING": "⟳",
        "CRASHED": "✗",
        "REMOVED": "−"
    }.get(status, "?")

    print(f"stratinit [{_active_service}]: {status_icon} {status}")
    print(f"  Deployment: {dep['id'][:8]}...")
    print(f"  Created: {created} UTC")

    if status == "SUCCESS" and PUBLIC_URL:
        try:
            req = urllib.request.Request(f"{PUBLIC_URL}/health", method="GET")
            req.add_header("User-Agent", "stratinit-railway-client/1.0")
            with urllib.request.urlopen(req, timeout=5) as resp:
                health = json.loads(resp.read().decode("utf-8"))
                print(f"  Health: ✓ {health}")
        except Exception as e:
            print(f"  Health: ✗ unreachable ({e})")


def cmd_logs(args):
    """Show deployment logs (build logs for failed builds, runtime logs otherwise)."""
    dep_data = get_deployments(1)
    if not dep_data:
        print("No deployments found")
        return

    edges = dep_data.get("deployments", {}).get("edges", [])
    if not edges:
        print("No deployments found")
        return

    dep = edges[0]["node"]
    dep_id = dep["id"]
    status = dep["status"]

    logs_data = get_deployment_logs(dep_id, args.lines)
    logs = (logs_data or {}).get("deploymentLogs", [])
    log_type = "runtime"

    if not logs:
        logs_data = get_build_logs(dep_id, args.lines)
        logs = (logs_data or {}).get("buildLogs", [])
        log_type = "build"

    print(f"=== {log_type.title()} logs for {_active_service} deployment {dep_id[:8]}... ({status}) ===\n")

    if not logs:
        print("No log entries")
        return

    seen = set()
    for log in sorted(logs, key=lambda x: x.get("timestamp", "")):
        msg = log.get("message", "")
        if not msg or msg in seen:
            continue
        seen.add(msg)

        severity = log.get("severity", "info").lower()
        timestamp = log.get("timestamp", "")[:19].replace("T", " ")

        if severity == "error":
            print(f"[{timestamp}] ERROR: {msg}")
        else:
            print(f"[{timestamp}] {msg}")


def cmd_deployments(args):
    """List recent deployments."""
    data = get_deployments(args.limit)
    if not data:
        print("No data returned")
        return

    edges = data.get("deployments", {}).get("edges", [])
    if not edges:
        print("No deployments found")
        return

    print(f"Recent deployments for {_active_service} (showing {len(edges)}):\n")
    for edge in edges:
        dep = edge["node"]
        status = dep["status"]
        created = dep["createdAt"][:19].replace("T", " ")

        status_icon = {"SUCCESS": "✓", "FAILED": "✗", "BUILDING": "⟳", "DEPLOYING": "⟳"}.get(status, "?")
        print(f"  {status_icon} {dep['id'][:12]}  {status:12}  {created}")


def cmd_setvar(args):
    """Set an environment variable."""
    result = set_variable(args.key, args.value)
    if result and result.get("variableUpsert"):
        print(f"✓ Set {args.key}={args.value} on {_active_service}")
        print("  (This will trigger a new deployment)")
    else:
        print(f"✗ Failed to set {args.key}")


def cmd_getvar(args):
    """Get environment variable(s)."""
    data = get_variables()
    if not data:
        print("No data returned")
        return

    variables = data.get("variablesForServiceDeployment", {})

    if args.key:
        if args.key in variables:
            print(f"{args.key}={variables[args.key]}")
        else:
            print(f"Variable '{args.key}' not found")
    else:
        print(f"Environment variables for {_active_service}:\n")
        for key in sorted(variables.keys()):
            value = variables[key]
            if any(s in key.lower() for s in ["secret", "password", "token", "key"]):
                display = value[:4] + "..." if len(value) > 4 else "***"
            else:
                display = value if len(value) < 50 else value[:47] + "..."
            print(f"  {key}={display}")


def cmd_delvar(args):
    """Delete an environment variable."""
    result = delete_variable(args.key)
    if result and result.get("variableDelete"):
        print(f"✓ Deleted {args.key} from {_active_service}")
        print("  (This will trigger a new deployment)")
    else:
        print(f"✗ Failed to delete {args.key}")


def cmd_redeploy(args):
    """Trigger a new deployment."""
    print(f"Triggering deployment for {_active_service}...")
    result = trigger_deployment()
    if result:
        print("✓ Deployment triggered")
        print("  Run 'railway.py status' to check progress")
    else:
        print("✗ Failed to trigger deployment")


def cmd_health(args):
    """Check the health endpoint."""
    if not PUBLIC_URL:
        print("No PUBLIC_URL configured. Generate a domain in Railway first.")
        print("Then update PUBLIC_URL in scripts/railway.py")
        sys.exit(1)
    try:
        req = urllib.request.Request(f"{PUBLIC_URL}/health", method="GET")
        req.add_header("User-Agent", "stratinit-railway-client/1.0")
        with urllib.request.urlopen(req, timeout=10) as resp:
            health = json.loads(resp.read().decode("utf-8"))
            print(json.dumps(health, indent=2))
    except urllib.error.HTTPError as e:
        print(f"HTTP Error {e.code}: {e.reason}")
        sys.exit(1)
    except urllib.error.URLError as e:
        print(f"Connection Error: {e.reason}")
        sys.exit(1)


def cmd_update(args):
    """Update service instance settings."""
    updates = {}
    if args.start_command is not None:
        updates["startCommand"] = args.start_command
    if args.build_command is not None:
        updates["buildCommand"] = args.build_command
    if args.healthcheck_path is not None:
        updates["healthcheckPath"] = args.healthcheck_path
    if args.healthcheck_timeout is not None:
        updates["healthcheckTimeout"] = args.healthcheck_timeout
    if args.root_directory is not None:
        updates["rootDirectory"] = args.root_directory
    if args.watch_paths is not None:
        updates["watchPatterns"] = args.watch_paths

    if not updates:
        print("No updates specified. Use --start-command, --build-command, etc.")
        return

    result = update_service_instance(updates)
    if result and result.get("serviceInstanceUpdate"):
        print(f"✓ Updated {_active_service}:")
        for key, value in updates.items():
            print(f"  {key} = {value}")
        print("  (This will trigger a new deployment)")
    else:
        print(f"✗ Failed to update {_active_service}")


def cmd_domain(args):
    """Manage service domains."""
    if args.action == "create":
        result = create_domain()
        if result and result.get("serviceDomainCreate"):
            domain = result["serviceDomainCreate"]["domain"]
            print(f"✓ Domain created: https://{domain}")
        else:
            print("✗ Failed to create domain")
    elif args.action == "list":
        result = get_domains()
        if not result or not result.get("domains"):
            print("No domains found")
            return
        domains = result["domains"]
        service_domains = domains.get("serviceDomains", [])
        custom_domains = domains.get("customDomains", [])
        if service_domains:
            print(f"Railway domains for {_active_service}:")
            for d in service_domains:
                print(f"  https://{d['domain']}")
        if custom_domains:
            print(f"Custom domains for {_active_service}:")
            for d in custom_domains:
                print(f"  https://{d['domain']}")
        if not service_domains and not custom_domains:
            print(f"No domains for {_active_service}. Use 'domain create' to generate one.")


def cmd_add_postgres(args):
    """Add a PostgreSQL database to the project."""
    print("Adding PostgreSQL database to project...")
    result = add_postgres_plugin()
    if result and result.get("templateDeploy"):
        print("✓ PostgreSQL service created")
        print("  The database is being provisioned. Once ready:")
        print("  1. Run 'railway.py projects' to find the Postgres service ID")
        print("  2. Set DATABASE_URL on server: railway.py setvar DATABASE_URL '${{Postgres.DATABASE_URL}}'")
    else:
        print("✗ Failed to add PostgreSQL")


def cmd_setup(args):
    """Create and configure the stratinit Railway project from scratch."""
    import secrets
    import re

    GITHUB_REPO = "fil512/stratinit"
    GITHUB_BRANCH = "master"
    SCRIPT_PATH = os.path.abspath(__file__)

    print("=== stratinit Railway Setup ===\n")

    # Step 1: Create project
    print("Step 1: Creating project 'stratinit'...")
    result = create_project("stratinit")
    if not result or not result.get("projectCreate"):
        print("✗ Failed to create project")
        print("  If 'stratinit' already exists, run 'railway.py projects' to get its ID")
        print("  then manually update PROJECT_ID/ENVIRONMENT_ID in this script.")
        sys.exit(1)

    proj = result["projectCreate"]
    project_id = proj["id"]
    envs = proj["environments"]["edges"]
    environment_id = envs[0]["node"]["id"]
    env_name = envs[0]["node"]["name"]
    print(f"✓ Project ID:     {project_id}")
    print(f"✓ Environment ID: {environment_id} ({env_name})")

    # Step 2: Create server service connected to GitHub
    print(f"\nStep 2: Creating 'server' service (GitHub: {GITHUB_REPO}@{GITHUB_BRANCH})...")
    svc_result = create_service(project_id, "server", repo=GITHUB_REPO, branch=GITHUB_BRANCH)
    if not svc_result or not svc_result.get("serviceCreate"):
        print("✗ Failed to create service")
        sys.exit(1)
    service_id = svc_result["serviceCreate"]["id"]
    print(f"✓ Service ID: {service_id}")

    # Step 3: Add Postgres
    print("\nStep 3: Adding PostgreSQL...")
    pg_result = add_postgres_explicit(project_id, environment_id)
    if pg_result and pg_result.get("templateDeploy"):
        print("✓ PostgreSQL provisioning started")
        print("  (DATABASE_URL will be available as a Railway reference variable)")
    else:
        print("⚠ PostgreSQL setup may have failed — check Railway dashboard")

    # Step 4: Generate JWT secret and set env vars
    print("\nStep 4: Setting environment variables...")
    jwt_secret = secrets.token_hex(32)
    vars_to_set = [
        ("PORT", "8081"),
        ("STRATINIT_JWT_SECRET", jwt_secret),
        ("SPRING_DATASOURCE_URL", "${{Postgres.DATABASE_URL}}"),
    ]
    for name, value in vars_to_set:
        r = set_variable_explicit(project_id, environment_id, service_id, name, value)
        if r and r.get("variableUpsert"):
            display = value if "SECRET" not in name else value[:8] + "..."
            print(f"  ✓ {name}={display}")
        else:
            print(f"  ✗ Failed to set {name}")

    # Step 5: Create public domain
    print("\nStep 5: Creating public domain...")
    domain_result = create_domain_explicit(service_id, environment_id)
    public_url = ""
    if domain_result and domain_result.get("serviceDomainCreate"):
        domain = domain_result["serviceDomainCreate"]["domain"]
        public_url = f"https://{domain}"
        print(f"✓ Domain: {public_url}")
    else:
        print("⚠ Domain creation failed — create one in the Railway dashboard")

    # Step 6: Trigger first deploy
    print("\nStep 6: Triggering first deployment...")
    dep_result = trigger_deployment_explicit(project_id, environment_id, service_id)
    if dep_result:
        print("✓ Deployment triggered")
    else:
        print("⚠ Could not trigger deployment — push to GitHub to deploy")

    # Step 7: Update this script with the new IDs
    print("\nStep 7: Updating scripts/railway.py with project IDs...")
    with open(SCRIPT_PATH, "r") as f:
        content = f.read()

    content = re.sub(r'PROJECT_ID = ""', f'PROJECT_ID = "{project_id}"', content)
    content = re.sub(r'ENVIRONMENT_ID = ""  # production', f'ENVIRONMENT_ID = "{environment_id}"  # production', content)
    content = re.sub(r'"server": "",  # Spring Boot backend service ID', f'"server": "{service_id}",  # Spring Boot backend service ID', content)
    if public_url:
        content = re.sub(r'PUBLIC_URL = ""', f'PUBLIC_URL = "{public_url}"', content)

    with open(SCRIPT_PATH, "w") as f:
        f.write(content)
    print("✓ scripts/railway.py updated with project IDs")

    print(f"""
=== Setup Complete ===

Project ID:     {project_id}
Environment ID: {environment_id}
Service ID:     {service_id}
Public URL:     {public_url or "(not set)"}

IMPORTANT — save this JWT secret somewhere safe:
  STRATINIT_JWT_SECRET = {jwt_secret}

Next steps:
  1. Commit the updated scripts/railway.py:
       git add scripts/railway.py && git commit -m 'chore: add Railway project IDs'
  2. Check deployment progress:
       python scripts/railway.py status
  3. Once deployed, verify health:
       python scripts/railway.py health
""")


def cmd_projects(args):
    """List all projects."""
    query = """
    query {
        projects(first: 20) {
            edges {
                node {
                    id
                    name
                    environments { edges { node { id name } } }
                    services { edges { node { id name } } }
                }
            }
        }
    }
    """
    data = graphql_query(query)
    if not data:
        print("No data returned")
        return

    for proj_edge in data.get("projects", {}).get("edges", []):
        proj = proj_edge["node"]
        print(f"\n=== {proj['name']} ===")
        print(f"ID: {proj['id']}")

        envs = proj.get("environments", {}).get("edges", [])
        if envs:
            print("Environments:")
            for env_edge in envs:
                env = env_edge["node"]
                print(f"  - {env['name']} ({env['id']})")

        svcs = proj.get("services", {}).get("edges", [])
        if svcs:
            print("Services:")
            for svc_edge in svcs:
                svc = svc_edge["node"]
                print(f"  - {svc['name']} ({svc['id']})")


def cmd_raw(args):
    """Execute a raw GraphQL query."""
    data = graphql_query(args.query)
    if data:
        print(json.dumps(data, indent=2))


def cmd_schema(args):
    """Inspect a GraphQL schema type."""
    query = """
    query IntrospectType($name: String!) {
        __type(name: $name) {
            name
            kind
            fields { name type { name kind ofType { name } } }
            inputFields { name type { name kind ofType { name } } }
        }
    }
    """
    data = graphql_query(query, {"name": args.type})
    if data and data.get("__type"):
        print(json.dumps(data["__type"], indent=2))
    else:
        print(f"Type '{args.type}' not found")


def main():
    global _active_service

    parser = argparse.ArgumentParser(
        description="Railway API client for stratinit",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  railway.py status              # Check server deployment status
  railway.py logs -n 50          # View last 50 log lines
  railway.py setvar FOO bar      # Set environment variable
  railway.py getvar              # List all variables
  railway.py redeploy            # Trigger new deployment
  railway.py projects            # List all projects (useful for finding IDs)
"""
    )
    parser.add_argument(
        "--service", "-s",
        choices=list(SERVICES.keys()),
        default="server",
        help="Target service (default: server)"
    )
    subparsers = parser.add_subparsers(dest="command", help="Command")

    # setup (one-time project creation)
    subparsers.add_parser("setup", help="Create Railway project and configure it (run once)")

    # status
    subparsers.add_parser("status", help="Show deployment status")

    # logs
    logs_p = subparsers.add_parser("logs", help="Show deployment logs")
    logs_p.add_argument("-n", "--lines", type=int, default=100, help="Number of lines (default: 100)")

    # deployments
    dep_p = subparsers.add_parser("deployments", help="List recent deployments")
    dep_p.add_argument("-n", "--limit", type=int, default=10, help="Number to show (default: 10)")

    # setvar
    setvar_p = subparsers.add_parser("setvar", help="Set environment variable")
    setvar_p.add_argument("key", help="Variable name")
    setvar_p.add_argument("value", help="Variable value")

    # getvar
    getvar_p = subparsers.add_parser("getvar", help="Get environment variable(s)")
    getvar_p.add_argument("key", nargs="?", help="Variable name (optional, shows all if omitted)")

    # delvar
    delvar_p = subparsers.add_parser("delvar", help="Delete environment variable")
    delvar_p.add_argument("key", help="Variable name")

    # update (service instance settings)
    update_p = subparsers.add_parser("update", help="Update service settings")
    update_p.add_argument("--start-command", help="Start command")
    update_p.add_argument("--build-command", help="Build command")
    update_p.add_argument("--healthcheck-path", help="Health check path (e.g. /health)")
    update_p.add_argument("--healthcheck-timeout", type=int, help="Health check timeout in seconds")
    update_p.add_argument("--root-directory", help="Root directory for builds")
    update_p.add_argument("--watch-paths", nargs="+", help="Watch paths")

    # domain
    domain_p = subparsers.add_parser("domain", help="Manage service domains")
    domain_p.add_argument("action", choices=["list", "create"], help="Action to perform")

    # add-postgres
    subparsers.add_parser("add-postgres", help="Add PostgreSQL database to project")

    # redeploy
    subparsers.add_parser("redeploy", help="Trigger a new deployment")

    # health
    subparsers.add_parser("health", help="Check health endpoint")

    # projects
    subparsers.add_parser("projects", help="List all projects")

    # raw
    raw_p = subparsers.add_parser("raw", help="Execute raw GraphQL query")
    raw_p.add_argument("query", help="GraphQL query string")

    # schema
    schema_p = subparsers.add_parser("schema", help="Inspect GraphQL schema type")
    schema_p.add_argument("type", help="Type name to inspect")

    args = parser.parse_args()
    _active_service = args.service

    commands = {
        "setup": cmd_setup,
        "status": cmd_status,
        "logs": cmd_logs,
        "deployments": cmd_deployments,
        "setvar": cmd_setvar,
        "getvar": cmd_getvar,
        "delvar": cmd_delvar,
        "update": cmd_update,
        "domain": cmd_domain,
        "add-postgres": cmd_add_postgres,
        "redeploy": cmd_redeploy,
        "health": cmd_health,
        "projects": cmd_projects,
        "raw": cmd_raw,
        "schema": cmd_schema,
    }

    if args.command in commands:
        commands[args.command](args)
    else:
        parser.print_help()


if __name__ == "__main__":
    main()
