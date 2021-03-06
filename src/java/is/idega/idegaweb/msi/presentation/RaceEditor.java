package is.idega.idegaweb.msi.presentation;

import is.idega.idegaweb.msi.bean.TimeTransmitterRentProperties;
import is.idega.idegaweb.msi.business.ConverterUtility;
import is.idega.idegaweb.msi.data.Event;
import is.idega.idegaweb.msi.data.Race;
import is.idega.idegaweb.msi.data.RaceCategory;
import is.idega.idegaweb.msi.data.RaceEvent;
import is.idega.idegaweb.msi.data.RaceType;
import is.idega.idegaweb.msi.data.Season;
import is.idega.idegaweb.msi.util.MSIConstants;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.ejb.FinderException;

import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Table2;
import com.idega.presentation.TableCell2;
import com.idega.presentation.TableRow;
import com.idega.presentation.TableRowGroup;
import com.idega.presentation.text.Break;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.DateInput;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.user.data.Group;
import com.idega.util.IWTimestamp;
import com.idega.util.StringUtil;

public class RaceEditor extends RaceBlock {
	
	protected static final String PARAMETER_ACTION = "msi_prm_action";
	protected static final String PARAMETER_SEASON_PK = "prm_season_pk";
	protected static final String PARAMETER_RACE_PK = "prm_race_pk";
	protected static final String PARAMETER_RACE_NAME = "prm_race_name";
	protected static final String PARAMETER_RACE_DATE = "prm_race_date";
	protected static final String PARAMETER_LAST_REGISTRATION_DATE = "prm_last_registration_date";
	protected static final String PARAMETER_LAST_REGISTRATION_DATE_PRICE1 = "prm_last_registration_date_price1";
	protected static final String PARAMETER_RACE_EVENTS = "prm_race_event";
	protected static final String PARAMETER_RACE_EVENTS_PRICE = "prm_race_event_price";
	protected static final String PARAMETER_RACE_EVENTS_PRICE2 = "prm_race_event_price2";
	protected static final String PARAMETER_RACE_EVENTS_TIME_TRANSMITTER_PRICE = "prm_race_event_tt_price";
	protected static final String PARAMETER_RACE_EVENTS_TIME_TRANSMITTER_PRICE_ON = "prm_race_event_tt_price_on";
	protected static final String PARAMETER_RACE_EVENTS_CHIP = "prm_race_event_chip";
	protected static final String PARAMETER_RACE_EVENTS_CHIP_PRICE = "prm_race_event_chip_price";
	protected static final String PARAMETER_RACE_CATEGORY = "prm_race_category";
	protected static final String PARAMETER_RACE_TYPE = "prm_race_type";
	protected static final String PARAMETER_RACE_TEAM_COUNT = "prm_race_team_count";
	
	private static final int ACTION_VIEW = 1;
	private static final int ACTION_EDIT = 2;
	private static final int ACTION_NEW = 3;
	protected static final int ACTION_SAVE_NEW = 4;
	private static final int ACTION_SAVE_EDIT = 5;
	

	public void main(IWContext iwc) throws Exception {
		init(iwc);
	}
	
	protected void init(IWContext iwc) throws Exception {
		switch (parseAction(iwc)) {
			case ACTION_VIEW:
				showList(iwc);
				break;

			case ACTION_NEW:
				showEditor(iwc);
				break;

			case ACTION_EDIT:
				showEditor(iwc);
				break;

			case ACTION_SAVE_NEW:
				saveNew(iwc);
				showList(iwc);
				break;

			case ACTION_SAVE_EDIT:
				saveEdit(iwc);
				showList(iwc);
				break;
		}
	}

	private int parseAction(IWContext iwc) {
		if (iwc.isParameterSet(PARAMETER_ACTION)) {
			return Integer.parseInt(iwc.getParameter(PARAMETER_ACTION));
		}
		return ACTION_VIEW;
	}
	
	public void showList(IWContext iwc) throws RemoteException, FinderException {
		Form form = new Form();
				
		Table2 table = new Table2();
		table.setWidth("100%");
		table.setCellpadding(0);
		table.setCellspacing(0);
				
		Collection seasons = getRaceBusiness(iwc).getSeasons();
		Iterator seasonIt = seasons.iterator();
		DropdownMenu seasonDropDown = (DropdownMenu) getStyledInterface(new DropdownMenu(PARAMETER_SEASON_PK)); 
		seasonDropDown.addMenuElement("", localize("race_editor.select_season","Select season"));
		while (seasonIt.hasNext()) {
			Group season = (Group)seasonIt.next();
			seasonDropDown.addMenuElement(season.getPrimaryKey().toString(), localize(season.getName(),season.getName()));
		}
		seasonDropDown.setToSubmit();
		
		Collection races = null;
		if (iwc.isParameterSet(PARAMETER_SEASON_PK)) {
			String seasonID = iwc.getParameter(PARAMETER_SEASON_PK);
			seasonDropDown.setSelectedElement(seasonID);
			Season selectedSeason = getRaceBusiness(iwc).getSeasonByGroupId(Integer.valueOf(seasonID));
		    String[] types = {MSIConstants.GROUP_TYPE_RACE};
			races = ConverterUtility.getInstance().convertSeasonToGroup(selectedSeason).getChildGroups(types, true); 
		}
		
		TableRowGroup group = table.createHeaderRowGroup();
		TableRow row = group.createRow();
		TableCell2 cell = row.createHeaderCell();
		cell.setCellHorizontalAlignment(Table2.HORIZONTAL_ALIGNMENT_LEFT);
		cell.add(seasonDropDown);
		group.createRow().createCell().setHeight("20");
		
		if (iwc.isParameterSet(PARAMETER_SEASON_PK)) {
			row = group.createRow();
			cell = row.createHeaderCell();
			cell.setCellHorizontalAlignment(Table2.HORIZONTAL_ALIGNMENT_LEFT);
			cell.add(new Text(localize("race_editor.race_name", "Name")));

			cell = row.createHeaderCell();
			cell.setCellHorizontalAlignment(Table2.HORIZONTAL_ALIGNMENT_LEFT);
			cell.add(new Text(localize("race_editor.race_type", "Race type")));

			cell = row.createHeaderCell();
			cell.setCellHorizontalAlignment(Table2.HORIZONTAL_ALIGNMENT_LEFT);
			cell.add(new Text(localize("race_editor.race_category", "Race category")));

			cell = row.createHeaderCell();
			cell.setCellHorizontalAlignment(Table2.HORIZONTAL_ALIGNMENT_LEFT);
			cell.add(new Text(localize("race_editor.race_date", "Date")));

			cell = row.createHeaderCell();
			cell.setCellHorizontalAlignment(Table2.HORIZONTAL_ALIGNMENT_LEFT);
			cell.add(new Text(localize("race_editor.race_last_registration_price1", "Last registration lower price")));

			cell = row.createHeaderCell();
			cell.setCellHorizontalAlignment(Table2.HORIZONTAL_ALIGNMENT_LEFT);
			cell.add(new Text(localize("race_editor.race_last_registration", "Last registration")));
		}
				
		group = table.createBodyRowGroup();
		
		if (races != null) {
			Iterator iter = races.iterator();
			Race race;
			
			while (iter.hasNext()) {
				row = group.createRow();
				race = ConverterUtility.getInstance().convertGroupToRace((Group) iter.next());
				try {
					Link edit = new Link(getEditIcon(localize("edit", "Edit")));
					edit.addParameter(PARAMETER_SEASON_PK, iwc.getParameter(PARAMETER_SEASON_PK));
					edit.addParameter(PARAMETER_RACE_PK, race.getPrimaryKey().toString());
					edit.addParameter(PARAMETER_ACTION, ACTION_EDIT);
								
					cell = row.createCell();
					cell.add(new Text(race.getName()));
					cell = row.createCell();
					if (race.getRaceType() != null) {
						cell.add(new Text(localize(race.getRaceType().getRaceType(), race.getRaceType().getRaceType())));
					} else {
						cell.add(new Text(""));						
					}
					cell = row.createCell();
					if (race.getRaceCategory() != null) {
						cell.add(new Text(localize(race.getRaceCategory().getCategoryKey(), race.getRaceCategory().getCategoryKey())));
					} else {
						cell.add(new Text(""));						
					}
					cell = row.createCell();
					if (race.getRaceDate() != null) {
						cell.add(new Text(new IWTimestamp(race.getRaceDate()).getDateString("dd.MM.yyyy")));						
					} else {
						cell.add(new Text(""));												
					}
					cell = row.createCell();
					if (race.getLastRegistrationDatePrice1() != null) {
						cell.add(new Text(new IWTimestamp(race.getLastRegistrationDatePrice1()).getDateString("dd.MM.yyyy")));
					} else {
						cell.add(new Text(""));												
					}
					cell = row.createCell();
					if (race.getLastRegistrationDate() != null) {
						cell.add(new Text(new IWTimestamp(race.getLastRegistrationDate()).getDateString("dd.MM.yyyy")));
					} else {
						cell.add(new Text(""));												
					}					

					row.createCell().add(edit);
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		form.add(table);
		form.add(new Break());
		if (iwc.isParameterSet(PARAMETER_SEASON_PK)) {
			SubmitButton newLink = (SubmitButton) getButton(new SubmitButton(localize("race_editor.new_race", "New race"), PARAMETER_ACTION, String.valueOf(ACTION_NEW)));
			form.add(newLink);
		}
		add(form);
	}

	public void showEditor(IWContext iwc) throws java.rmi.RemoteException, FinderException {
		String raceID = iwc.getParameter(PARAMETER_RACE_PK);
		Form form = new Form();
		form.maintainParameter(PARAMETER_SEASON_PK);
		form.maintainParameter(PARAMETER_RACE_PK);
		TextInput race = new TextInput(PARAMETER_RACE_NAME);
		DateInput raceDate = new DateInput(PARAMETER_RACE_DATE);
		DateInput lastRegistrationDate = new DateInput(PARAMETER_LAST_REGISTRATION_DATE);
		DateInput lastRegistrationDatePrice1 = new DateInput(PARAMETER_LAST_REGISTRATION_DATE_PRICE1);

		Collection raceTypes = this.getRaceBusiness(iwc).getRaceTypes();
		Iterator it = raceTypes.iterator();
		DropdownMenu typeDropDown = (DropdownMenu) getStyledInterface(new DropdownMenu(PARAMETER_RACE_TYPE)); 
		typeDropDown.addMenuElement("", localize("race_editor.select_race_type","Select race type"));
		while (it.hasNext()) {
			RaceType type = (RaceType) it.next();
			typeDropDown.addMenuElement(type.getPrimaryKey().toString(), localize(type.getRaceType(),type.getRaceType()));
		}


		Collection raceCategories = this.getRaceBusiness(iwc).getRaceCategories();
		it = raceCategories.iterator();
		DropdownMenu categoryDropDown = (DropdownMenu) getStyledInterface(new DropdownMenu(PARAMETER_RACE_CATEGORY)); 
		categoryDropDown.addMenuElement("", localize("race_editor.select_race_category","Select race category"));
		while (it.hasNext()) {
			RaceCategory category = (RaceCategory) it.next();
			categoryDropDown.addMenuElement(category.getPrimaryKey().toString(), localize(category.getCategoryKey(),category.getCategoryKey()));
		}
				
		Layer layer = new Layer(Layer.DIV);
		layer.setStyleClass(STYLENAME_FORM_ELEMENT);
		Label label = new Label(localize("race_editor.race", "Race"), race);
		layer.add(label);
		layer.add(race);
		form.add(layer);
		form.add(new Break());

		layer = new Layer(Layer.DIV);
		layer.setStyleClass(STYLENAME_FORM_ELEMENT);
		label = new Label(localize("race_editor.race_type", "Type"), typeDropDown);
		layer.add(label);
		layer.add(typeDropDown);
		form.add(layer);
		form.add(new Break());

		layer = new Layer(Layer.DIV);
		layer.setStyleClass(STYLENAME_FORM_ELEMENT);
		label = new Label(localize("race_editor.race_category", "Category"), categoryDropDown);
		layer.add(label);
		layer.add(categoryDropDown);
		form.add(layer);
		form.add(new Break());
		
		layer = new Layer(Layer.DIV);
		layer.setStyleClass(STYLENAME_FORM_ELEMENT);
		label = new Label();
		label.setLabel(localize("race_editor.race_date", "Race date"));
		layer.add(label);
		layer.add(raceDate);
		form.add(layer);
		form.add(new Break());
		
		layer = new Layer(Layer.DIV);
		layer.setStyleClass(STYLENAME_FORM_ELEMENT);
		label = new Label(localize("race_editor.last_registration_date_price1", "Last registration date price 1"), lastRegistrationDatePrice1);
		layer.add(label);
		layer.add(lastRegistrationDatePrice1);
		form.add(layer);
		form.add(new Break());

		layer = new Layer(Layer.DIV);
		layer.setStyleClass(STYLENAME_FORM_ELEMENT);
		label = new Label(localize("race_editor.last_registration_date", "Last registration date"), lastRegistrationDate);
		layer.add(label);
		layer.add(lastRegistrationDate);
		form.add(layer);
		form.add(new Break());

		SubmitButton save = null;
		Map prices = null;
		Map prices2 = null;
		Map timeTransmitterPrices = null;
		//Map chips = null;
		//Map chipPrices = null;
		Map eventTeamCount = null;
		
		Map eventIDList = null;
		
		if (raceID != null) {
			save = (SubmitButton) getButton(new SubmitButton(localize("save", "Save"), PARAMETER_ACTION, String.valueOf(ACTION_SAVE_EDIT)));

			Group selectedGroupRace = getRaceBusiness(iwc).getSeasonByGroupId(Integer.valueOf(raceID.toString()));
			Race selectedRace = ConverterUtility.getInstance().convertGroupToRace(selectedGroupRace);
			race.setValue(selectedRace.getName());
			race.setDisabled(true);
			if (selectedRace.getRaceDate() != null) { 
				raceDate.setDate(new IWTimestamp(selectedRace.getRaceDate()).getDate());
			}
			if (selectedRace.getLastRegistrationDate() != null) { 
				lastRegistrationDate.setDate(new IWTimestamp(selectedRace.getLastRegistrationDate()).getDate());
			}
			
			if (selectedRace.getLastRegistrationDatePrice1() != null) {
				lastRegistrationDatePrice1.setDate(new IWTimestamp(selectedRace.getLastRegistrationDatePrice1()).getDate());
			}			
			if (selectedRace.getRaceCategory() != null) {
				categoryDropDown.setSelectedElement(selectedRace.getRaceCategory().getPrimaryKey().toString());
			}
			
			if (selectedRace.getRaceType() != null) {
				typeDropDown.setSelectedElement(selectedRace.getRaceType().getPrimaryKey().toString());
			}
			
			prices = getPricesMapForRace(iwc, selectedRace);
			prices2 = getPrices2MapForRace(iwc, selectedRace);
			timeTransmitterPrices = getTimeTransmitterPricesForRace(iwc, selectedRace);
			//chips = getChipsMapForRace(iwc, selectedRace);
			//chipPrices = getChipPricesMapForRace(iwc, selectedRace);
			eventTeamCount = getEventTeamCountMapForRace(iwc, selectedRace);
			eventIDList = getRaceBusiness(iwc).getEventsForRace(selectedRace);
		} else {
			save = (SubmitButton) getButton(new SubmitButton(localize("save", "Save"), PARAMETER_ACTION, String.valueOf(ACTION_SAVE_NEW)));
		}

		Table2 eventsTable = new Table2();
		TableRow tr = eventsTable.createHeaderRowGroup().createRow();
//		Headers
		tr.createHeaderCell();
		tr.createHeaderCell();
		tr.createHeaderCell().add(getText(localize("reace_editor.registration", "Registration")));
		tr.createHeaderCell().add(getText(localize("reace_editor.subsequent_registration", "Subsequent registration")));
		tr.createHeaderCell().add(getText(localize("reace_editor.team_count", "Team count")));
		tr.createHeaderCell().add(getText(localize("reace_editor.time_transmitter_rent_enabled", "Time transmitter rent enabled")));
		tr.createHeaderCell().add(getText(localize("reace_editor.time_transmitter_price", "Time transmitter price")));
		form.add(eventsTable);
		Collection events = getRaceBusiness(iwc).getEvents();
		String notAValidNumberError = localize("not_a_valid_float", "Not a valid number");
		if (events != null) {
			it = events.iterator();
			while (it.hasNext()) {
				Event event = (Event) it.next();
				CheckBox check = new CheckBox(PARAMETER_RACE_EVENTS, event.getName());
				TextInput price = new TextInput(PARAMETER_RACE_EVENTS_PRICE + "_" + event.getName());
				price.setAsFloat(notAValidNumberError);
				TextInput price2 = new TextInput(PARAMETER_RACE_EVENTS_PRICE2 + "_" + event.getName());
				price2.setAsFloat(notAValidNumberError);
				TextInput timeTransmitterPrice = new TextInput(PARAMETER_RACE_EVENTS_TIME_TRANSMITTER_PRICE + "_" + event.getName());
				timeTransmitterPrice.setAsFloat(notAValidNumberError);
				
				CheckBox timeTransmitterPriceOn = new CheckBox(PARAMETER_RACE_EVENTS_TIME_TRANSMITTER_PRICE_ON + "_" + event.getName(),"on");
				
				/*CheckBox hasChip = new CheckBox(PARAMETER_RACE_EVENTS_CHIP + "_" + event.getName(), event.getName());
				TextInput chipPrice = new TextInput(PARAMETER_RACE_EVENTS_CHIP_PRICE + "_" + event.getName());
				chipPrice.setAsFloat(localize("not_a_valid_float", "Not a valid number"));*/
				DropdownMenu teamCount = (DropdownMenu) getStyledInterface(new DropdownMenu(PARAMETER_RACE_TEAM_COUNT + "_" + event.getName()));
				teamCount.addMenuElement(1, "1");
				teamCount.addMenuElement(2, "2");
				teamCount.addMenuElement(3, "3");
				teamCount.setSelectedElement(1);

				if (prices != null) {
					Float eventPrice = (Float) prices.get(event.getName());
					if (eventPrice != null) {
						price.setValue(eventPrice.toString());
					}
				}
				if (prices2 != null) {
					Float eventPrice = (Float) prices2.get(event.getName());
					if (eventPrice != null) {
						price2.setValue(eventPrice.toString());
					}
				}
				if (timeTransmitterPrices != null) {
					TimeTransmitterRentProperties properties = (TimeTransmitterRentProperties) timeTransmitterPrices.get(event.getName());
					if (properties != null) {
						timeTransmitterPrice.setValue(properties.getPrice());
						timeTransmitterPriceOn.setChecked(properties.isRentOn());
					}
				}

/*				if (chips != null) {
					Boolean eventChip = (Boolean) chips.get(event.getName());
					if (eventChip == null) {
						hasChip.setChecked(false);
					} else {
						if (eventChip.booleanValue()) {  //NULLPOINTER!!!!
							hasChip.setChecked(true);
						} else {
							hasChip.setChecked(false);						
						}
					}
				}

				if (chipPrices != null) {
					Float eventChipPrice = (Float) chipPrices.get(event.getName());
					if (eventChipPrice != null) {
						chipPrice.setValue(eventChipPrice.toString());
					}
				}*/

				
				check.setToEnableWhenChecked(price);
				check.setToEnableWhenChecked(price2);
				check.setToEnableWhenChecked(teamCount);
				check.setToEnableWhenChecked(timeTransmitterPriceOn);
				check.setToEnableWhenChecked(timeTransmitterPrice);
				
				check.setToDisableWhenUnchecked(price);
				check.setToDisableWhenUnchecked(price2);
				check.setToDisableWhenUnchecked(teamCount);
				check.setToDisableWhenUnchecked(timeTransmitterPriceOn);
				check.setToDisableWhenUnchecked(timeTransmitterPrice);
				
				if (eventIDList != null) {
					if (eventIDList.containsKey(event.getName())) {
						check.setChecked(true);
						form.add(new HiddenInput(PARAMETER_RACE_EVENTS, event.getName()));
					} else {
						price.setDisabled(true);
						price2.setDisabled(true);
						teamCount.setDisabled(true);
						timeTransmitterPriceOn.setDisabled(true);
						timeTransmitterPrice.setDisabled(true);
					}
				}
				
				tr = eventsTable.createRow();
				label = new Label(event.getName(), check);
				tr.createCell().add(check);
				tr.createCell().add(label);
				tr.createCell().add(price);
				tr.createCell().add(price2);
				//layer.add(hasChip);
				//layer.add(chipPrice);
				tr.createCell().add(teamCount);
				tr.createCell().add(timeTransmitterPriceOn);
				tr.createCell().add(timeTransmitterPrice);
			}
		}

		SubmitButton cancel = (SubmitButton) getButton(new SubmitButton(localize("cancel", "Cancel"), PARAMETER_ACTION, String.valueOf(ACTION_VIEW)));

		form.add(save);
		form.add(cancel);

		add(form);
	}
	
	private Map getPricesMapForRace(IWContext iwc, Race race) throws FinderException, RemoteException {
		Group gRace = ConverterUtility.getInstance().convertRaceToGroup(race);
		
		String[] types = { MSIConstants.GROUP_TYPE_RACE_EVENT };
		Collection events = getGroupBusiness(iwc).getChildGroups(gRace, types, true);
		if (events != null) {
			Iterator it = events.iterator();
			Map map = new HashMap();
			
			while (it.hasNext()) {
				Group gEvent = (Group) it.next();
				RaceEvent event = ConverterUtility.getInstance().convertGroupToRaceEvent(gEvent);
				map.put(event.getEventID(), new Float(event.getPrice()));
			}
			
			return map;
		}

		
		return null;
	}

	private Map getPrices2MapForRace(IWContext iwc, Race race) throws FinderException, RemoteException {
		Group gRace = ConverterUtility.getInstance().convertRaceToGroup(race);
		
		String[] types = { MSIConstants.GROUP_TYPE_RACE_EVENT };
		Collection events = getGroupBusiness(iwc).getChildGroups(gRace, types, true);
		if (events != null) {
			Iterator it = events.iterator();
			Map map = new HashMap();
			
			while (it.hasNext()) {
				Group gEvent = (Group) it.next();
				RaceEvent event = ConverterUtility.getInstance().convertGroupToRaceEvent(gEvent);
				map.put(event.getEventID(), new Float(event.getPrice2()));
			}
			
			return map;
		}

		
		return null;
	}
	private Map getTimeTransmitterPricesForRace(IWContext iwc, Race race) throws FinderException, RemoteException {
		Group gRace = ConverterUtility.getInstance().convertRaceToGroup(race);
		
		String[] types = { MSIConstants.GROUP_TYPE_RACE_EVENT };
		Collection events = getGroupBusiness(iwc).getChildGroups(gRace, types, true);
		if (events != null) {
			Iterator it = events.iterator();
			Map map = new HashMap();
			
			while (it.hasNext()) {
				Group gEvent = (Group) it.next();
				RaceEvent event = ConverterUtility.getInstance().convertGroupToRaceEvent(gEvent);
				TimeTransmitterRentProperties properties = new TimeTransmitterRentProperties();
				properties.setPrice(String.valueOf(event.getTimeTransmitterPrice()));
				properties.setRentOn(event.isTimeTransmitterPriceOn());
				map.put(event.getEventID(), properties);
			}
			
			return map;
		}

		
		return null;
	}

/*	private Map getChipsMapForRace(IWContext iwc, Race race) throws FinderException, RemoteException {
		Group gRace = ConverterUtility.getInstance().convertRaceToGroup(race);
		
		String[] types = { MSIConstants.GROUP_TYPE_RACE_EVENT };
		Collection events = getGroupBusiness(iwc).getChildGroups(gRace, types, true);
		if (events != null) {
			Iterator it = events.iterator();
			Map map = new HashMap();
			
			while (it.hasNext()) {
				Group gEvent = (Group) it.next();
				RaceEvent event = ConverterUtility.getInstance().convertGroupToRaceEvent(gEvent);
				map.put(event.getEventID(), new Boolean(event.getHasChip()));
			}
			
			return map;
		}

		
		return null;
	}

	private Map getChipPricesMapForRace(IWContext iwc, Race race) throws FinderException, RemoteException {
		Group gRace = ConverterUtility.getInstance().convertRaceToGroup(race);
		
		String[] types = { MSIConstants.GROUP_TYPE_RACE_EVENT };
		Collection events = getGroupBusiness(iwc).getChildGroups(gRace, types, true);
		if (events != null) {
			Iterator it = events.iterator();
			Map map = new HashMap();
			
			while (it.hasNext()) {
				Group gEvent = (Group) it.next();
				RaceEvent event = ConverterUtility.getInstance().convertGroupToRaceEvent(gEvent);
				map.put(event.getEventID(), new Float(event.getChipPrice()));
			}
			
			return map;
		}

		
		return null;
	}*/

	private Map getEventTeamCountMapForRace(IWContext iwc, Race race) throws FinderException, RemoteException {
		Group gRace = ConverterUtility.getInstance().convertRaceToGroup(race);
		
		String[] types = { MSIConstants.GROUP_TYPE_RACE_EVENT };
		Collection events = getGroupBusiness(iwc).getChildGroups(gRace, types, true);
		if (events != null) {
			Iterator it = events.iterator();
			Map map = new HashMap();
			
			while (it.hasNext()) {
				Group gEvent = (Group) it.next();
				RaceEvent event = ConverterUtility.getInstance().convertGroupToRaceEvent(gEvent);
				map.put(event.getEventID(), new Float(event.getPrice2()));
			}
			
			return map;
		}

		
		return null;
	}

	
	
	public void saveNew(IWContext iwc) throws java.rmi.RemoteException, FinderException {
		String seasonID = iwc.getParameter(PARAMETER_SEASON_PK);
		String name = iwc.getParameter(PARAMETER_RACE_NAME);
		String raceDate = iwc.getParameter(PARAMETER_RACE_DATE);
		String lastRegistration = iwc.getParameter(PARAMETER_LAST_REGISTRATION_DATE);
		String lastRegistrationPrice1 = iwc.getParameter(PARAMETER_LAST_REGISTRATION_DATE_PRICE1);
		String type = iwc.getParameter(PARAMETER_RACE_TYPE);
		String category = iwc.getParameter(PARAMETER_RACE_CATEGORY);

		
		String events[] = iwc.getParameterValues(PARAMETER_RACE_EVENTS);
		Map prices = null;
		Map prices2 = null;
		Map timeTransmitterPrices = null;
		//Map chips = null;
		//Map chipPrices = null;
		Map teamCount = null;
		if (events != null && events.length > 0) {
			prices = new HashMap();
			prices2 = new HashMap();
			timeTransmitterPrices = new HashMap(); 
			//chips = new HashMap();
			//chipPrices = new HashMap();
			teamCount = new HashMap();
			for (int i = 0; i < events.length; i++) {
				prices.put(events[i], iwc.getParameter(PARAMETER_RACE_EVENTS_PRICE + "_" + events[i]));
				prices2.put(events[i], iwc.getParameter(PARAMETER_RACE_EVENTS_PRICE2 + "_" + events[i]));
				//chips.put(events[i], iwc.getParameter(PARAMETER_RACE_EVENTS_CHIP + "_" + events[i]));
				//chipPrices.put(events[i], iwc.getParameter(PARAMETER_RACE_EVENTS_CHIP_PRICE + "_" + events[i]));
				teamCount.put(events[i], iwc.getParameter(PARAMETER_RACE_TEAM_COUNT + "_" + events[i]));
				TimeTransmitterRentProperties timeTransmitterRentProperties = new TimeTransmitterRentProperties();
				String pricetString = iwc.getParameter(PARAMETER_RACE_EVENTS_TIME_TRANSMITTER_PRICE + "_" + events[i]);
				timeTransmitterRentProperties.setPrice(StringUtil.isEmpty(pricetString) ? "0" : pricetString);
				timeTransmitterRentProperties.setRentOn("on".equals(iwc.getParameter(PARAMETER_RACE_EVENTS_TIME_TRANSMITTER_PRICE_ON + "_" + events[i])));
				timeTransmitterPrices.put(events[i], timeTransmitterRentProperties);
			}
		}
		
		Race race = getRaceBusiness(iwc).createRace(seasonID, name, raceDate, lastRegistration, lastRegistrationPrice1, type, category);
		getRaceBusiness(iwc).addEventsToRace(race, events, prices, prices2, teamCount,timeTransmitterPrices);		
	}
	
	private void saveEdit(IWContext iwc) throws java.rmi.RemoteException, FinderException {
		String raceID = iwc.getParameter(PARAMETER_RACE_PK);
		Race race = null;
		if (raceID != null) {
			try {
				race = ConverterUtility.getInstance().convertGroupToRace(new Integer(raceID));
			} 
			catch (FinderException e){
				//no year found, nothing saved
			}
		}
		if (race != null) {
			String raceDate = iwc.getParameter(PARAMETER_RACE_DATE);
			String lastRegistration = iwc.getParameter(PARAMETER_LAST_REGISTRATION_DATE);
			String lastRegistrationPrice1 = iwc.getParameter(PARAMETER_LAST_REGISTRATION_DATE_PRICE1);
			String type = iwc.getParameter(PARAMETER_RACE_TYPE);
			String category = iwc.getParameter(PARAMETER_RACE_CATEGORY);
			
			String events[] = iwc.getParameterValues(PARAMETER_RACE_EVENTS);
			Map prices = null;
			Map prices2 = null;
			Map timeTransmitterPrices = null;
			//Map chips = null;
			//Map chipPrices = null;
			Map teamCount = null;
			if (events != null && events.length > 0) {
				prices = new HashMap();
				prices2 = new HashMap();
				teamCount = new HashMap();
				timeTransmitterPrices = new HashMap(); 
				for (int i = 0; i < events.length; i++) {
					prices.put(events[i], iwc.getParameter(PARAMETER_RACE_EVENTS_PRICE + "_" + events[i]));
					prices2.put(events[i], iwc.getParameter(PARAMETER_RACE_EVENTS_PRICE2 + "_" + events[i]));
					//chips.put(events[i], iwc.getParameter(PARAMETER_RACE_EVENTS_CHIP + "_" + events[i]));
					//chipPrices.put(events[i], iwc.getParameter(PARAMETER_RACE_EVENTS_CHIP_PRICE + "_" + events[i]));
					teamCount.put(events[i], iwc.getParameter(PARAMETER_RACE_TEAM_COUNT + "_" + events[i]));
					TimeTransmitterRentProperties timeTransmitterRentProperties = new TimeTransmitterRentProperties();
					String pricetString = iwc.getParameter(PARAMETER_RACE_EVENTS_TIME_TRANSMITTER_PRICE + "_" + events[i]);
					timeTransmitterRentProperties.setPrice(StringUtil.isEmpty(pricetString) ? "0" : pricetString);
					timeTransmitterRentProperties.setRentOn("on".equals(iwc.getParameter(PARAMETER_RACE_EVENTS_TIME_TRANSMITTER_PRICE_ON + "_" + events[i])));
					timeTransmitterPrices.put(events[i], timeTransmitterRentProperties);
				}
			}
			
			getRaceBusiness(iwc).updateRace(race, raceDate, lastRegistration, lastRegistrationPrice1, type, category);
			getRaceBusiness(iwc).addEventsToRace(race, events, prices, prices2, teamCount,timeTransmitterPrices);		
			
			race.store();
		}
	}
}