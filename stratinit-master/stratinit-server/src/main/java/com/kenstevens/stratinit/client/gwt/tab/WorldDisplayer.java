package com.kenstevens.stratinit.client.gwt.tab;

import com.kenstevens.stratinit.client.gwt.event.GWTSectorsArrivedEventHandler;
import com.kenstevens.stratinit.client.gwt.model.GWTSector;
import com.kenstevens.stratinit.client.gwt.model.StatusReporter;
import com.kenstevens.stratinit.client.gwt.service.GWTDataManager;
import com.kenstevens.stratinit.client.gwt.status.StatusSetter;
import com.kenstevens.stratinit.client.gwt.widget.play.tab.CanvasTab;
import com.kenstevens.stratinit.client.gwt.widget.play.tab.CityTab;
import com.kenstevens.stratinit.client.gwt.widget.play.tab.CmdTab;
import com.kenstevens.stratinit.client.gwt.widget.play.tab.MapCanvas;
import com.kenstevens.stratinit.client.gwt.widget.play.tab.SectorTab;
import com.kenstevens.stratinit.client.gwt.widget.play.tab.UnitTab;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

public class WorldDisplayer {
	private final Window window = new Window();
	private MapCanvas mapCanvas;
	private TabSet topTabSet;

	private UnitTab unitTab;
	private CityTab cityTab;
	private final int size;
	private VLayout mapLayout;
	private Label sectorLabel;
	final String[] status = new String[] {"","","",""};

	// TODO GWT need nation, and need to limit what can be built in cities
	public WorldDisplayer(int gameId, int size) {
		this.size = size;
		window.setTitle("Game " + gameId);
		window.setWidth(1024);
		window.setHeight(768);
		window.setTop(0);
		window.setLeft(0);

		VLayout vlayout = new VLayout(10);

		Label warningLabel = new Label("Warning: This client is experimental.  You should download the full client for your platform.");
		warningLabel.setHeight(20);
		vlayout.addMember(warningLabel);
		HLayout hLayout = new HLayout();
		hLayout.setWidth100();
		hLayout.setHeight100();
		vlayout.addMember(hLayout);

		mapLayout = new VLayout();
		mapLayout.setWidth100();
		mapLayout.setOverflow(Overflow.AUTO);
		sectorLabel = new Label();
		mapCanvas = new MapCanvas(size, mapLayout, sectorLabel);
		mapLayout.addMember(mapCanvas);
		hLayout.addMember(mapLayout);

		VLayout tabColumn = new VLayout();
		tabColumn.setWidth(320);
		topTabSet = new TabSet();
		topTabSet.setWidth100();
		topTabSet.setHeight(600);
		topTabSet.setPickerButtonSize(20);

		addTab("Sector", new SectorTab());
		unitTab = new UnitTab();
		addTab("Units", unitTab);
		cityTab = new CityTab();
		addTab("Cities", cityTab);
		final CmdTab cmdTab = new CmdTab();
		addTab("Cmds", cmdTab);
		tabColumn.addMember(topTabSet);

		final HTMLFlow statusFlow = new HTMLFlow();

		statusFlow.setWidth100();
		statusFlow.setHeight(80);
		tabColumn.addMember(statusFlow);
		sectorLabel.setWidth(40);
		sectorLabel.setHeight(20);
		HStack bottomRow = new HStack();
		bottomRow.setHeight(20);
		bottomRow.setWidth100();
		bottomRow.addMember(sectorLabel);
		final IButton button = new IButton("Update");
        button.setWidth(120);
        button.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	new DataUpdater().update();
            }
        });
        bottomRow.addMember(button);
		tabColumn.addMember(bottomRow);
		hLayout.addMember(tabColumn);

		window.addItem(vlayout);

		StatusReporter.setStatusSetter(new StatusSetter() {
			@Override
			public void setText(String text) {
				statusFlow.setContents(text);
			}
			@Override
			public void addText(String message) {
				status[0] = status[1];
				status[1] = status[2];
				status[2] = status[3];
				status[3] = message;
				statusFlow.setContents(status[0]+"<BR>\n"+
						status[1]+"<BR>\n"+status[2]+"<BR>\n"+status[3]);
				cmdTab.addText(message);
			}

		});

		addHandlers();
	}

	private void addHandlers() {

		GWTSectorsArrivedEventHandler sahandler = new GWTSectorsArrivedEventHandler() {

			@Override
			public void receiveNewSectors(GWTSector[][] sectors) {
				mapCanvas.sectorsChanged(sectors);
			}

		};
		GWTDataManager.addHandler(GWTSectorsArrivedEventHandler.TYPE, sahandler);
	}

	private void addTab(String title, CanvasTab canvasTab) {
		Tab tab = new Tab(title);
		tab.setPane(canvasTab);
		topTabSet.addTab(tab);
	}

	public void refreshData() {
		unitTab.refreshData();
	}

	public void show() {
		window.show();
	}

	public int getSize() {
		return size;
	}

	public Window getWindow() {
		return window;
	}
}
