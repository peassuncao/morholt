package br.fapema.morholt.web.client.gui;

import static br.fapema.morholt.web.shared.TableEnum.ALLOCATION;
import static br.fapema.morholt.web.shared.TableEnum.COLLECT;
import static br.fapema.morholt.web.shared.TableEnum.HISTORIC;
import static br.fapema.morholt.web.shared.TableEnum.PROFILE;
import static br.fapema.morholt.web.shared.TableEnum.PROJECT;
import static br.fapema.morholt.web.shared.TableEnum.TEMPLATE_ANDROID;
import static br.fapema.morholt.web.shared.TableEnum.STATE;
import static br.fapema.morholt.web.shared.TableEnum.USERS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import br.fapema.morholt.web.client.gui.grid.AboutView;
import br.fapema.morholt.web.client.gui.grid.CallAfterEditionInterface;
import br.fapema.morholt.web.client.gui.grid.EnterInterface;
import br.fapema.morholt.web.client.gui.grid.GridView;
import br.fapema.morholt.web.client.gui.grid.SitioDoFisicoView;
import br.fapema.morholt.web.client.gui.grid.enter.EnterContent;
import br.fapema.morholt.web.client.gui.grid.enter.EnterType;
import br.fapema.morholt.web.client.gui.grid.enter.EnterTypes;
import br.fapema.morholt.web.client.gui.grid.historic.HistoricGridView;
import br.fapema.morholt.web.client.gui.model.Model;
import br.fapema.morholt.web.client.gui.model.Profile;
import br.fapema.morholt.web.client.gui.util.Ajax;
import br.fapema.morholt.web.client.service.MyServiceClientImpl;
import br.fapema.morholt.web.shared.TemplateReader;
import br.fapema.morholt.web.shared.value.State;

public class MainView extends Composite implements AsyncCallback {
	private static final Logger log = Logger.getLogger(MainView.class.getName());
	private DockLayoutPanel basePanel = new DockLayoutPanel(Unit.PCT);
	private MyServiceClientImpl myService;
	private Map<String, GridView> mapKindToGrid = new HashMap<String, GridView>();
	private GridView gridViewProjects;
	private GridView gridViewUsers;
	private GridView gridViewProfiles;
	private GridView gridViewAndroidTemplate;
	private HistoricGridView gridViewHistoric;
	private Map<String, EnterInterface> tabWidgets;
	private Map<String, List<DataSource>> mapTemplateNameToDatasources;
	private Map<String, List<Model>> mapKindToSmallTables;
	private Dictionary loginInfo;
	private VerticalPanel westAuxiliarPanel;
	private TabLayoutPanel tabLayoutPanel;

	public MainView(Dictionary loginInfo, MyServiceClientImpl myService,
			Map<String, List<DataSource>> mapCollectTemplateToDatasources, List<Model> templateModels) {
		this.myService = myService;
		this.mapTemplateNameToDatasources = mapCollectTemplateToDatasources;
		mapKindToSmallTables = new HashMap<String, List<Model>>();
		mapKindToSmallTables.put(TEMPLATE_ANDROID.kind, templateModels);
		mapKindToSmallTables.put(STATE.kind, State.models());
		this.loginInfo = loginInfo;

		initWidget(basePanel);

		basePanel.setWidth("100%");
		basePanel.setHeight("100%");
		basePanel.setStylePrimaryName("mainPanel");

		basePanel.addWest(createWestPanel(), 18);
		basePanel.addNorth((Widget) new MainViewNorthPanel(), 17);
		basePanel.add(createTabLayout(myService));

		

	}

	private Profile getProfile() {
		return myService.getUser().getProfile();
	}

	private DockLayoutPanel createWestPanel() { // user comes from
												// gwt_hosting.jsp
		SafeHtml safeHtml = SafeHtmlUtils.fromString(
				"Logout\nusuário: " + getUserEmailFromJavascript() + " \nperfil: " + getProfile().getName());

		Anchor link = new Anchor(safeHtml, getLogoutUrlFromJavascript());

		VerticalPanel verticalPanel = new VerticalPanel();
		Label titleLabel = new Label("Sítio do físico");
		titleLabel.addStyleName("titleLabel");
		verticalPanel.add(titleLabel); // TODO
															// internationalization
		verticalPanel.add(link);

		DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.PCT);
		dockLayoutPanel.addNorth(verticalPanel, 13);

		westAuxiliarPanel = new VerticalPanel();

		westAuxiliarPanel.setVisible(false);
		dockLayoutPanel.add(westAuxiliarPanel);
		return dockLayoutPanel;
	}

	private TabLayoutPanel createTabLayout(MyServiceClientImpl myService) {
		tabLayoutPanel = new TabLayoutPanel(2, Unit.EM);
		tabLayoutPanel.addStyleDependentName("tabPanel");
		tabLayoutPanel.setHeight("100%");
		tabLayoutPanel.setWidth("100%");

		gridViewProjects = createGridViewProject(myService);
		tabLayoutPanel.add(gridViewProjects, "Coleções");

		if (getProfile().authenticate(USERS.kind, Profile.CRUDEL.LIST)) {
			gridViewUsers = new GridView(USERS.kind, USERS.key, Ajax.call(new UserCallBack(), "users callback"),
					myService, DataSourceDefaults.obtainDataSourcesUsers(), null, 100, 2, null, mapKindToSmallTables, null); // TODO
																														// last
																														// arg
			gridViewUsers.setRefinedSearchAuxiliarWidget(westAuxiliarPanel);
			mapKindToGrid.put(PROJECT.kind, gridViewUsers);
			tabLayoutPanel.add(gridViewUsers, "Usuários");
		}

		if (getProfile().authenticate(PROFILE.kind, Profile.CRUDEL.LIST) ) {
			gridViewProfiles = new GridView(PROFILE.kind, PROFILE.key,
					Ajax.call(new ProfileCallBack(), "profile callback"), myService,
					DataSourceDefaults.obtainDataSourcesProfiles(), null, 100, 1, new ProfileCallAfterEdition(),
					mapKindToSmallTables, null);
			if( getProfile().authenticate(PROFILE.kind, Profile.CRUDEL.CREATE)) {
				gridViewProfiles.setRefinedSearchAuxiliarWidget(westAuxiliarPanel);
				mapKindToGrid.put(PROJECT.kind, gridViewProfiles);
				tabLayoutPanel.add(gridViewProfiles, "Perfis");
			
			}
		}

		if (getProfile().authenticate(TEMPLATE_ANDROID.kind, Profile.CRUDEL.LIST)
				&& getProfile().authenticate(TEMPLATE_ANDROID.kind, Profile.CRUDEL.READ)) {
			gridViewAndroidTemplate = new GridView(TEMPLATE_ANDROID.kind, TEMPLATE_ANDROID.key,
					Ajax.call(new AndroidTemplateCallBack(), "android template callback"), myService,
					DataSourceDefaults.obtainDataSourcesAndroidTemplate(), null, 100, 1, null, mapKindToSmallTables, null); // TODO
																														// last
																														// arg
			gridViewAndroidTemplate.setRefinedSearchAuxiliarWidget(westAuxiliarPanel);
			mapKindToGrid.put(TEMPLATE_ANDROID.kind, gridViewAndroidTemplate);
			tabLayoutPanel.add(gridViewAndroidTemplate, "Android Template");
		}

		if (getProfile().authenticate(HISTORIC.kind, Profile.CRUDEL.LIST)) {
			gridViewHistoric = new HistoricGridView(myService);
			gridViewHistoric.setRefinedSearchAuxiliarWidget(westAuxiliarPanel, true);
			tabLayoutPanel.add(gridViewHistoric, "Histórico");
		}

		tabLayoutPanel.add(new SitioDoFisicoView(westAuxiliarPanel), "Sítio do Físico");
		tabLayoutPanel.add(new AboutView(westAuxiliarPanel), "Sobre");

		tabLayoutPanel.addSelectionHandler(new SelectionHandler<Integer>() {

			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				Integer selectedItem = event.getSelectedItem();
				Widget widget = tabLayoutPanel.getWidget(selectedItem);
				if (widget instanceof AuxiliarInterface) {
					AuxiliarInterface auxiliarInterface = (AuxiliarInterface) widget;
					auxiliarInterface.prepareAuxiliaryPanel();
				} else {
					throw new RuntimeException("all tabs must implement AuxiliarInterface");
				}
			}
		});
		return tabLayoutPanel;
	}

	private GridView createGridViewProject(MyServiceClientImpl myService) {
		log.info("mapKindToSmallTables:" + mapKindToSmallTables);
		tabWidgets = new HashMap<String, EnterInterface>();
		HashMap<String, EnterInterface> collectTemplateWidgets = new HashMap<String, EnterInterface>();
		if (getProfile().authenticate(COLLECT.kind, Profile.CRUDEL.LIST)) { // TODO
																			// this
																			// says
																			// security
																			// is
																			// same
																			// for
																			// everything
																			// added
																			// from
																			// datasource(only
																			// collect)
			for (String collectTemplate : mapTemplateNameToDatasources.keySet()) {

				List<DataSource> dataSources = mapTemplateNameToDatasources.get(collectTemplate);
				// dataSources.add(createFeatureDataSource());
				GridView collectGrid = new GridView(COLLECT.kind, null, null, myService, dataSources, null, 100, 2,
						null, mapKindToSmallTables, "project", true);
				collectGrid.setWidth("100%");
				collectGrid.setHeight("100%");
				collectGrid.setRefinedSearchAuxiliar(westAuxiliarPanel, true);
				collectTemplateWidgets.put(collectTemplate, collectGrid);
			}
		}

		if (getProfile().authenticate(ALLOCATION.kind, Profile.CRUDEL.LIST)) {
			Allocation userAllocation = new Allocation(myService, getProfile());
			userAllocation.setHeight("100%");
			userAllocation.setRefinedSearchAuxiliarWidget(westAuxiliarPanel);
			tabWidgets.put("alocação", userAllocation);
		}

		if (tabWidgets.isEmpty())
			tabWidgets = null;

		EnterContent enterContent = new EnterContent(myService, new EnterTypes(EnterType.Grid), "NP", "enterKind",
				tabWidgets, collectTemplateWidgets, "project_name", "Coleção");

		GridView gridViewProjects = new GridView(PROJECT.kind, PROJECT.key, this, myService,
				DataSourceDefaults.obtainDataSourcesProject(), enterContent, 100, 2, null, mapKindToSmallTables, null);

		gridViewProjects.setRefinedSearchAuxiliarWidget(westAuxiliarPanel);
		mapKindToGrid.put("gridKind", gridViewProjects);

		return gridViewProjects;
	}

	public void collectWasUpdated(String kind, String keyName, List<Model> list) {
		mapKindToGrid.get(kind).updateGrid(list); // TODO used?
	}

	@Override
	public void onFailure(Throwable caught) {
		caught.printStackTrace();
		log.log(Level.SEVERE, "onFailure: " + caught.getMessage());
		Window.alert("onFailure");
	}

	@Override
	public void onSuccess(Object result) {
		log.info("MainView: callback sucess (from project gridview)");
		if (result != null && result instanceof List<?>) {
			List<Model> models = (List<Model>) result;
			gridViewProjects.updateGrid(models);
		}

		// success on deletion
		else if (result != null && result instanceof String) {
			List<Model> newList = new ArrayList<Model>();
			for (Model model : gridViewProjects.getCurrentList()) {
				log.info("Model key value: " + model.get("Key"));
				if (!model.get("Key").equals(result)) {
					newList.add(model);
				}
			}
			log.info("Model list size: " + newList.size());
			gridViewProjects.updateGrid(newList);
		}
	}

	public class UserCallBack implements AsyncCallback {
		@Override
		public void onFailure(Throwable caught) {
			caught.printStackTrace();
			log.log(Level.SEVERE, "onFailure: " + caught.getMessage());
			Window.alert("onFailure");
		}

		@Override
		public void onSuccess(Object result) {
			if (result != null && result instanceof List<?>) {
				List<Model> models = (List<Model>) result;
				if(!getProfile().authenticate(PROFILE.kind, Profile.CRUDEL.CREATE)) 
					removeAdminUsers(models);
				gridViewUsers.updateGrid(models);
				Map<String, List<Model>> mapKindToSmallTables = new HashMap<String, List<Model>>();
				mapKindToSmallTables.put("users", models);

				if(gridViewHistoric != null)
					gridViewHistoric.setMapKindToSmallTables(mapKindToSmallTables);
			}
		}
	}
	
	/**
	 * so that only an admin can add an admin
	 * @param models
	 */
	private void removeAdminUsers(List<Model> models) {
		for (Iterator<Model> iterator = models.iterator(); iterator.hasNext();) {
			Model model = iterator.next();
			if("admin".equals(model.get("profile"))) {
				iterator.remove();
			}
		} 
	}

	boolean gridViewProjectsAdded = false;

	public class AndroidTemplateCallBack implements AsyncCallback<List<Model>> {
		@Override
		public void onFailure(Throwable caught) {
			caught.printStackTrace();
			log.log(Level.SEVERE, "onFailure: " + caught.getMessage());
			Window.alert("onFailure");
		}

		@Override
		public void onSuccess(List<Model> templateModels) {
			if (templateModels != null) {

				updateMapKindToSmallTables(templateModels);
				updateMapCollectTemplateToDatasources(templateModels);

				gridViewProjects = createGridViewProject(myService);
				tabLayoutPanel.remove(0);
				tabLayoutPanel.insert(gridViewProjects, "Coleções", 0);
				gridViewAndroidTemplate.updateGrid(templateModels);

				tabLayoutPanel.forceLayout();
				gridViewProjects.showGrid();

				if (!gridViewProjectsAdded) {
					tabLayoutPanel.selectTab(0);
					gridViewProjectsAdded = true;
				}
			}
		}

		private void updateMapKindToSmallTables(List<Model> templateModels) {
			if (mapKindToSmallTables == null)
				mapKindToSmallTables = new HashMap<String, List<Model>>();
			mapKindToSmallTables.put(TEMPLATE_ANDROID.kind, templateModels);
		}

		private void updateMapCollectTemplateToDatasources(List<Model> templateModels) {
			HashMap<String, List<DataSource>> mapTemplateToDatasources = new HashMap<String, List<DataSource>>();
			for (Model modelTemplate : templateModels) {
				mapTemplateToDatasources.putAll(TemplateReader.generateTemplateToDatasources(modelTemplate));
			}

			MainView.this.mapTemplateNameToDatasources = mapTemplateToDatasources;
		}
	}

	public class ProfileCallBack implements AsyncCallback<List<Model>> {
		@Override
		public void onFailure(Throwable caught) {
			caught.printStackTrace();
			log.log(Level.SEVERE, "onFailure: " + caught.getMessage());
			Window.alert("onFailure");
		}

		@Override
		public void onSuccess(List<Model> result) {
			if (result != null && result instanceof List<?>) {
				List<Model> models = result;
				if(!getProfile().authenticate(PROFILE.kind, Profile.CRUDEL.CREATE)) {
					removeAdminProfile(models);
				}
				
				gridViewProfiles.updateGrid(models);
				Map<String, List<Model>> mapKindToSmallTables = new HashMap<String, List<Model>>();
				mapKindToSmallTables.put("profile", models);
				
				gridViewUsers.setMapKindToSmallTables(mapKindToSmallTables);
				log.log(Level.FINER, "profileCallback ok");
			}
		}
	}
	
	/**
	 * so that only an admin can add an admin
	 * @param models
	 */
	private void removeAdminProfile(List<Model> models) {
		for (Iterator<Model> iterator = models.iterator(); iterator.hasNext();) {
			Model model = iterator.next();
			if("admin".equals(model.get("name"))) {
				iterator.remove();
				break;
			}
		} 
			
	}

	private String getUserEmailFromJavascript() {
		loginInfo.get("email");
		return loginInfo.get("email");
	}

	private String getLogoutUrlFromJavascript() {
		if(getUserEmailFromJavascript().equals("anonymous@ositiodofisico.appspot.com")) {
			return "/gwt_hosting.jsp";//MyEntryPoint.obtainLocalIp();
		}
		else return loginInfo.get("logoutUrl");
	}

	public class ProfileCallAfterEdition implements CallAfterEditionInterface<Model> {
		@Override
		public void call(Model model) {
			for (GridView grid : mapKindToGrid.values()) {
				grid.getProfile().setModel(model);
				grid.clear();
			}
		}
	}
}