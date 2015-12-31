package br.fapema.morholt.web.client.gui;

import static br.fapema.morholt.web.shared.TableEnum.ALLOCATION;
import static br.fapema.morholt.web.shared.TableEnum.USERS;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;

import br.fapema.morholt.web.client.gui.MyEntryPoint.TemplateAndroidCallBack;
import br.fapema.morholt.web.client.gui.grid.EnterInterface;
import br.fapema.morholt.web.client.gui.model.Model;
import br.fapema.morholt.web.client.gui.model.ModelList;
import br.fapema.morholt.web.client.gui.model.Profile;
import br.fapema.morholt.web.client.gui.model.Profile.CRUDEL;
import br.fapema.morholt.web.client.gui.util.Ajax;
import br.fapema.morholt.web.client.service.MyServiceClientImpl;
import br.fapema.morholt.web.shared.TableEnum;
//TODO allow multiple allocation for a user, perhaps create a grid for allocation (with combos: user and project)
public class Allocation extends Composite implements EnterInterface {

	private ListBox multiBoxLeft;
	private ListBox multiBoxRight;
	private MyServiceClientImpl myService;
	private Map<Model, String> userToAllocationKey;
	private ModelList recentMovedUsers;
	private Model enterSelectedModel;

	private List<Model> allAllocationModels;

	private ModelList users = new ModelList();
	private Panel refinedSearchAuxiliarWidget;
	private Button buttonListAllocation;
	private Profile profile;
	
	public Allocation(final MyServiceClientImpl myService, Profile profile) {
		this.myService = myService;
		this.profile = profile;
		
		DockLayoutPanel hPanel = new DockLayoutPanel(Unit.PCT);
		initWidget(hPanel);
		hPanel.setHeight(String.valueOf(Window.getClientHeight() * 10 / 100.0));
		hPanel.setWidth(String.valueOf(Window.getClientWidth() * 10 / 100.0));

		HorizontalPanel horizontalPanel = new HorizontalPanel();
		buttonListAllocation = new Button("atualizar");
		horizontalPanel.add(buttonListAllocation);
		buttonListAllocation.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				updateAllocationList();
			}
		});
		hPanel.addNorth(horizontalPanel, 5);

		AbsolutePanel multiBoxPanel = createMultiBoxPanelLeft();
		multiBoxLeft = createMultiboxLeft();
		multiBoxPanel.add(multiBoxLeft);
		hPanel.addWest(multiBoxPanel, 45);

		AbsolutePanel multiBoxPanelRight = createMultiBoxPanelRight();
		multiBoxRight = createMultiBoxRight();
		multiBoxPanelRight.add(multiBoxRight);
		hPanel.addEast(multiBoxPanelRight, 45);

		hPanel.add(createButtonsPanel());
	}

	@Override
	public void setRefinedSearchAuxiliarWidget(Panel widget) {
		this.refinedSearchAuxiliarWidget = widget;
	}
	
	private HorizontalPanel createButtonsPanel() {
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setHeight("100%");
		horizontalPanel.setWidth("100%");

		if (profile.authenticate(ALLOCATION.kind, Profile.CRUDEL.UPDATE) && profile.authenticate(ALLOCATION.kind, Profile.CRUDEL.DELETE)) {
			Button toLeftButton = createLeftButton();
			horizontalPanel.add(toLeftButton);
			horizontalPanel.setCellHorizontalAlignment(toLeftButton,
					HasHorizontalAlignment.ALIGN_RIGHT);
			horizontalPanel.setCellVerticalAlignment(toLeftButton,
					HasVerticalAlignment.ALIGN_MIDDLE);
		}

		if (profile.authenticate(ALLOCATION.kind, Profile.CRUDEL.UPDATE) && profile.authenticate(ALLOCATION.kind, Profile.CRUDEL.CREATE)) {
			Button toRightButton = createRightButton();
			horizontalPanel.add(toRightButton);
			horizontalPanel.setCellHorizontalAlignment(toRightButton,
					HasHorizontalAlignment.ALIGN_LEFT);
			horizontalPanel.setCellVerticalAlignment(toRightButton,
					HasVerticalAlignment.ALIGN_MIDDLE);
			toRightButton.setSize("30px", "4em");
		}
		
		return horizontalPanel;
	}

	private AbsolutePanel createMultiBoxPanelLeft() {
		AbsolutePanel multiBoxPanel = new AbsolutePanel();
		HTML title = new HTML("disponível");
		title.setHeight("5%");
		multiBoxPanel.add(title);
		multiBoxPanel.setHeight("100%");
		return multiBoxPanel;
	}

	private AbsolutePanel createMultiBoxPanelRight() {
		AbsolutePanel multiBoxPanelRight = new AbsolutePanel();
		HTML titleRight = new HTML("alocado(s)");
		titleRight.setHeight("5%");
		multiBoxPanelRight.add(titleRight);
		multiBoxPanelRight.setHeight("100%");
		return multiBoxPanelRight;
	}

	private ListBox createMultiboxLeft() {
		multiBoxLeft = new ListBox();
		multiBoxLeft.setMultipleSelect(true);
		multiBoxLeft.ensureDebugId("cwListBox-multiBox");
		multiBoxLeft.setHeight("95%");
		multiBoxLeft.setWidth("100%");
		return multiBoxLeft;
	}

	private ListBox createMultiBoxRight() {
		multiBoxRight = new ListBox();
		multiBoxRight.setMultipleSelect(true);
		multiBoxRight.ensureDebugId("cwListBox-multiBox2");
		multiBoxRight.setHeight("95%");
		multiBoxRight.setWidth("100%");
		return multiBoxRight;
	}

	private Button createLeftButton() {
		Button toLeftButton = new Button(" < ");
		toLeftButton.setSize("30px", "4em");
		toLeftButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				moveItemTo(multiBoxRight, multiBoxLeft);
			}
		});
		return toLeftButton;
	}

	private Button createRightButton() {
		Button toRightButton = new Button(" > ");
		toRightButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				moveItemTo(multiBoxLeft, multiBoxRight);
			}

		});
		return toRightButton;
	}

	private void moveItemTo(final ListBox multiBoxOrigin,
			final ListBox multiBoxDest) {
		moveItemsOnDatabase(multiBoxOrigin, multiBoxDest);

		TreeSet<String> orderedSet = createDestOrderedSet(multiBoxDest);

		addItemsBeingMovedToOrderedSetAndRemoveFromOrigin(multiBoxOrigin,
				orderedSet);

		removeAllFromDest(multiBoxDest);

		addOrderedSetToDest(multiBoxDest, orderedSet);
	}

	private void addOrderedSetToDest(final ListBox multiBoxDest,
			TreeSet<String> orderedDest) {
		for (String value : orderedDest) {
			multiBoxDest.addItem(value);
		}
	}

	private void removeAllFromDest(final ListBox multiBoxDest) {
		for (int j = multiBoxDest.getItemCount() - 1; j >= 0; j--) {
			multiBoxDest.removeItem(j);
		}
	}

	private void addItemsBeingMovedToOrderedSetAndRemoveFromOrigin(
			final ListBox multiBox, TreeSet<String> orderedDest) {
		for (int i = multiBox.getItemCount() - 1; i >= 0; i--) {
			if (multiBox.isItemSelected(i)) {
				String selectedUserName = multiBox.getValue(i);
				orderedDest.add(selectedUserName);
				multiBox.removeItem(i);
			}
		}
	}

	private TreeSet<String> createDestOrderedSet(final ListBox multiBoxDest) {
		TreeSet<String> orderedDest = new TreeSet<String>();
		for (int i = 0; i < multiBoxDest.getItemCount(); i++) {
			String itemText = multiBoxDest.getValue(i);
			orderedDest.add(itemText);
		}
		return orderedDest;
	}

	private void moveItemsOnDatabase(final ListBox multiBox,
			final ListBox multiBoxDest) {
		recentMovedUsers = obtainMovedUsers(multiBox);
		if (multiBoxDest.equals(multiBoxRight)) {
			myService
					.insertAllocations(enterSelectedModel.getKeyValue(),
							recentMovedUsers,
							Ajax.call(new InsertAllocationCallback(), Allocation.class + "moveItemsOnDatabase insertAllocations"));
		} else {

			ArrayList<String> keysToBeRemoved = new ArrayList<String>();
			for (Iterator<Model> iterator = userToAllocationKey.keySet()
					.iterator(); iterator.hasNext();) {
				Model user = iterator.next();
				if (recentMovedUsers.contains(user)) {
					keysToBeRemoved.add(userToAllocationKey.get(user));
					iterator.remove();
				}
			}
			myService.removeAllocations(keysToBeRemoved);
		}
	}

	private ModelList obtainMovedUsers(final ListBox multiBox) {
		ModelList movedUsers = new ModelList();
		for (int i = 0; i < multiBox.getItemCount(); i++) {
			if (multiBox.isItemSelected(i)) {
				movedUsers.add(users.get(multiBox.getItemText(i)));
			}
		}
		return movedUsers;
	}

	private class InsertAllocationCallback implements AsyncCallback {

		@Override
		public void onFailure(Throwable caught) {
			Window.alert("Error inserting allocation");
			throw new RuntimeException(caught);

		}

		@Override
		public void onSuccess(Object result) {
			List<String> stringKeys = (List<String>) result;
			for (int i = 0; i < stringKeys.size(); i++) {
				String allocationKey = stringKeys.get(i);
				Model recentMovedUser = recentMovedUsers.get(i);
				userToAllocationKey.put(recentMovedUser, allocationKey);
			}
		}
	}

	private class ListUsersCallback implements AsyncCallback<ModelList> {

		@Override
		public void onFailure(Throwable caught) {
			Window.alert("Error listing users");
			throw new RuntimeException(caught);
		}

		@Override
		public void onSuccess(ModelList result) {
			multiBoxLeft.clear();
			
			
			users = result;
			ModelList allUsersModels = new ModelList();
			allUsersModels.addAll(result);
			removeUsersAlreadyAllocated(allUsersModels);
			removeAnonymousUser(allUsersModels);
			
			for (int i = 0; i < multiBoxRight.getItemCount(); i++) {
				String allocatedUser = multiBoxRight.getItemText(i);
				allUsersModels.remove(allocatedUser);
			}
			for (Model user : allUsersModels) {
				multiBoxLeft.addItem(user.get("Name"));
			}
		}
		
		private void removeAnonymousUser(ModelList users) {
			for (Iterator<Model> iterator = users.iterator(); iterator.hasNext();) {
				Model model =  iterator.next();
				if("anonymous".equals(model.get("profile"))) {
					iterator.remove();
					break;
				}
				
			}
		}

		private List<Model> removeUsersAlreadyAllocated(List<Model> users) {
			for (Iterator<Model> iterator = users.iterator(); iterator
					.hasNext();) {
				Model user = iterator.next();
				for (Model allocation : allAllocationModels) {
					if (allocation.get("user").equals(
							user.get("Name"))) {
						
						iterator.remove();
						break;
					}
				}
			}
			return users;
		}
	}

	private class ListAllocationsCallback implements AsyncCallback {

		@Override
		public void onFailure(Throwable caught) {
			Window.alert("error listing allocation");
			throw new RuntimeException(caught);
		}

		@Override
		public void onSuccess(Object result) {
			multiBoxRight.clear();
			if (result != null && result instanceof List<?>) {
				userToAllocationKey = new HashMap<Model, String>();
				allAllocationModels = (List<Model>) result;
				for (Model allocationModel : allAllocationModels) {
					if (enterSelectedModel.getKeyValue().equals(
							allocationModel.get("projectId"))) {
						multiBoxRight.addItem(allocationModel.get("user"));
						userToAllocationKey.put(users.get(allocationModel.get("user")),
								allocationModel.get("Key"));
					}
				}
				

				Object tempTemplatePermission = profile.getProfileModel().getContentValues().get(TableEnum.USERS.kind + CRUDEL.LIST);
				profile.getProfileModel().getContentValues().put(TableEnum.USERS.kind + CRUDEL.LIST, true);
				myService.listItem(USERS.kind, USERS.key,
						null, null, 0, USERS.key, "Allocation",
						Ajax.call(new ListUsersCallback(), Allocation.class +  " " + ListAllocationsCallback.class + " listItem"));
				profile.getProfileModel().getContentValues().put(TableEnum.USERS.kind + CRUDEL.LIST, tempTemplatePermission);
			}
		}
	}

	@Override
	public void setEnterSelectedModel(Model model) {
		enterSelectedModel = model;
		updateAllocationList();
	}
	
	private void updateAllocationList() {
		myService.listAllocation(enterSelectedModel.getKeyValue(), "user",
				Ajax.call(new ListAllocationsCallback(), Allocation.class + "updateAllocationList listAllocation")); 
	}

	@Override
	public void clear() {
		updateAllocationList();
	}

	@Override
	public void prepareAuxiliaryPanel() {
		refinedSearchAuxiliarWidget.setVisible(false);
	}

	@Override
	public void setParentColumnLabel(String parentColumnLabel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setParentColumn(String parentColumn) {
		// TODO Auto-generated method stub
		
	}
}
