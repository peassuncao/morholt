package br.fapema.morholt.web.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.maps.client.MapOptions;
import com.google.gwt.maps.client.MapTypeId;
import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.maps.client.events.click.ClickMapEvent;
import com.google.gwt.maps.client.events.click.ClickMapHandler;
import com.google.gwt.maps.client.events.cursor.CursorChangeMapEvent;
import com.google.gwt.maps.client.events.cursor.CursorChangeMapHandler;
import com.google.gwt.maps.client.events.idle.IdleMapEvent;
import com.google.gwt.maps.client.events.idle.IdleMapHandler;
import com.google.gwt.maps.client.overlays.InfoWindow;
import com.google.gwt.maps.client.overlays.InfoWindowOptions;
import com.google.gwt.maps.client.overlays.Marker;
import com.google.gwt.maps.client.overlays.MarkerImage;
import com.google.gwt.maps.client.overlays.MarkerOptions;
import com.google.gwt.maps.utility.markerclustererplus.client.MarkerClusterer;
import com.google.gwt.maps.utility.markerclustererplus.client.MarkerClustererOptions;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;

import br.fapema.morholt.web.client.gui.basic.MapWidget;
import br.fapema.morholt.web.client.gui.basic.MyDialogBox;
import br.fapema.morholt.web.client.gui.grid.FlexTableEnum;
import br.fapema.morholt.web.client.gui.grid.FlexTableHelper;
import br.fapema.morholt.web.client.gui.model.Model;
import br.fapema.morholt.web.client.service.MyServiceClientImpl;
import br.fapema.morholt.web.shared.util.StringUtils;

public class MapView extends Composite implements AuxiliarInterface{
	private static final String PHOTO = "foto";
	private static final String GEOPOINT_Y = "geopointY";
	private static final String GEOPOINT_X = "geopointX";
	private static final Logger log = Logger.getLogger(MapView.class.getName());
	private ArrayList<LatLng> points;
	private Panel refinedSearchAuxiliarWidget;
	private MyServiceClientImpl myService;
	private int width;
	
	private static int additionalZindex = 1000;
	public MapView(List<Model> models, final List<DataSource> dataSources,
			int width, int height, MyServiceClientImpl myService) {
		DockLayoutPanel panel = new DockLayoutPanel(Unit.EM);
		initWidget(panel);
		this.myService = myService;
		this.width = width;
		
		addStyleDependentName("mapView");
		log.info("starting MapView");

		setSize(width + "px", height + "px");

		ArrayList<String> imageUrls = new ArrayList<String>();
		points = new ArrayList<LatLng>();
		for (Model model : models) {
			try {
				if(isGeolocationNull(model)) continue;
				points.add(LatLng.newInstance(
						Double.parseDouble(model.get(GEOPOINT_X)),
						Double.parseDouble(model.get(GEOPOINT_Y))));
				imageUrls.add(model.get(PHOTO));
			} catch (NumberFormatException e) {
				log.info("map coord numberFormatException: " + e.getMessage());
				throw e;
			} 
			catch (NullPointerException ne) {
				log.info("map coord null: " + ne.getMessage());
				throw ne;
			}
		}
		MapOptions myOptions = MapOptions.newInstance();

		if (!points.isEmpty()) {
			LatLng myLatLng = points.get(0); // TODO
			myOptions.setCenter(myLatLng);
		}

		myOptions.setZoom(19);
		myOptions.setMapTypeId(MapTypeId.ROADMAP);

		projectToIcon = new HashMap<String, String>();
		final MapWidget googleMap = new MapWidget(myOptions);
		
        //TODO EXTRA right click on cluster show area info like aoa, site
		
		googleMap.setSize(width + "px", height + "px");
		MarkerOptions markeroptions = MarkerOptions.newInstance();
		markeroptions.setMap(googleMap);
		
		final DialogBox dialogBox = new MyDialogBox(true);
		ArrayList<Marker> markers = new ArrayList<Marker>();
		for (final Model model : models) {
			try {
				if(isGeolocationNull(model)) continue;
				LatLng latLng = LatLng.newInstance(
						Double.parseDouble(model.get(GEOPOINT_X)),
						Double.parseDouble(model.get(GEOPOINT_Y)));
				final Marker marker = obtainMarker(dataSources, googleMap, markeroptions, dialogBox, model, latLng);
				markers.add(marker);
			} catch (NumberFormatException e) {
				log.info("map coord numberFormatException: " + e.getMessage());
				throw e;
			} 
			catch (NullPointerException ne) {
				log.info("map coord null: " + ne.getMessage());
				throw ne;
			}
		}
		
		
		
		MarkerClustererOptions markerClustererOptions = MarkerClustererOptions.newInstance();
		markerClustererOptions.setMaxZoom(20);
		MarkerClusterer.newInstance(
				googleMap, markers, markerClustererOptions);
		panel.add(googleMap);
		
		
		googleMap.addIdleHandler((new IdleMapHandler() {
			@Override
			public void onEvent(IdleMapEvent event) {
				googleMap.triggerResize();
			}
		}));
		
		log.info("map created");
	}

	private HashMap<String, String> projectToIcon;

	private Marker obtainMarker(final List<DataSource> dataSources, final MapWidget googleMap,
			MarkerOptions markeroptions, final DialogBox dialogBox, final Model model, LatLng latLng) {
		final Marker marker = Marker.newInstance(markeroptions);
		marker.setPosition(latLng);
		
		marker.setIcon(obtainIcon(model.get("project_name")));
		
		marker.addCursorChangeHandler(new CursorChangeMapHandler() {
			@Override
			public void onEvent(CursorChangeMapEvent event) {
				marker.setZindex(0);
			}
		});
		addClickHandler(dataSources, googleMap, dialogBox, model, marker);
		return marker;
	}
	
	private String obtainIcon(String project) {
		if(projectToIcon.containsKey(project))
			return projectToIcon.get(project);
		String value = "img/markers/ic" + projectToIcon.size()%15+".png";
		projectToIcon.put(project, value);
		return value;
	}
	
	
	
	private boolean isGeolocationNull(Model model) {
		return model.get(GEOPOINT_X) == null || model.get(GEOPOINT_Y) == null || "null".equals(model.get(GEOPOINT_X)) || "null".equals(model.get(GEOPOINT_Y));
	}
	private void addClickHandler(final List<DataSource> dataSources, final MapWidget googleMap,
			final DialogBox dialogBox, final Model model, final Marker marker) {
		marker.addClickHandler(new ClickMapHandler() {
			@Override
			public void onEvent(ClickMapEvent event) {
				marker.setZindex(marker.getZindex()-(additionalZindex++));
				drawInfoWindow(marker, event.getMouseEvent());
			}

			private void drawInfoWindow(
					com.google.gwt.maps.client.overlays.Marker marker,
					com.google.gwt.maps.client.events.MouseEvent mouseEvent) {

				if (marker == null || mouseEvent == null) {
					return;
				}

				final FlexTable table = FlexTableHelper.createTable(width, "xxx",
						model, dataSources, 2, FlexTableEnum.Detail, null, null, myService);
				FlexTableHelper.disableAllChildren(table);
				HorizontalPanel hp = new HorizontalPanel();
				
				if(StringUtils.isNotBlank(model.get(PHOTO))) {
					Image image = new Image(model.get(PHOTO) + "=s150");
					image.addDoubleClickHandler(new DoubleClickHandler() {
						@Override
						public void onDoubleClick(DoubleClickEvent event) {
							dialogBox.clear();
							Image image = new Image(model.get(PHOTO));
							dialogBox.add(image);
							dialogBox.setText("Foto");
							dialogBox.show();
						}
					});
	
					hp.add(image);
				}
				Button button = new Button("Detalhes");
				button.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						dialogBox.clear();
						ScrollPanel sp = new ScrollPanel();
						sp.setWidget(table);
						sp.setSize(Window.getClientWidth()-30+"px", "40em");
						dialogBox.setText("Detalhes np: " + StringUtils.defaultString(model.get("NP"), "(vazio)"));
						dialogBox.setWidget(sp);
						dialogBox.show();
					}
				});
				hp.add(new HTML("NP: " + StringUtils.defaultString(model.get("NP"), "(vazio)")));
				hp.add(button);

				InfoWindowOptions options = InfoWindowOptions.newInstance();
				options.setContent(hp);
				options.setDisableAutoPan(true);
				InfoWindow iw = InfoWindow.newInstance(options);

				iw.open(googleMap, marker);

			}
		});
	}
	@Override
	public void setRefinedSearchAuxiliarWidget(Panel refinedSearchAuxiliarWidget) {
		this.refinedSearchAuxiliarWidget = refinedSearchAuxiliarWidget;
		
	}
	@Override
	public void prepareAuxiliaryPanel() {
		refinedSearchAuxiliarWidget.clear();
		MapLegendView mapLegendView = new MapLegendView(projectToIcon);
		refinedSearchAuxiliarWidget.add(mapLegendView);
	}

}