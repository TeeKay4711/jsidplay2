package ui.musiccollection.search;

import java.io.File;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.SingularAttribute;

import ui.entities.collection.service.HVSCEntryService;
import ui.entities.collection.service.HVSCEntryService.HVSCEntries;


public abstract class SearchInIndexThread extends SearchThread {

	private SingularAttribute<?, ?> field;
	private Object fieldValue;
	private boolean caseSensitive;

	private HVSCEntryService hvscEntryService;
	private HVSCEntries state;

	public SearchInIndexThread(EntityManager em, boolean forward) {
		super(forward);
		hvscEntryService = new HVSCEntryService(em);
	}

	@Override
	public void run() {
		for (ISearchListener listener : fListeners) {
			listener.searchStart();
		}

		if (state == null) {
			state = hvscEntryService.search(field, fieldValue, caseSensitive,
					fForward);
		}
		while (!fAborted && (fForward ? state.next() : state.prev())) {
			String filePath = state.getPath();
			for (ISearchListener listener : fListeners) {
				List<File> file = getFiles(filePath);
				if (file.size() > 0) {
					listener.searchHit(file.get(file.size() - 1));
				}
			}
		}
		if (!fAborted) {
			state = null;
		}
		for (ISearchListener listener : fListeners) {
			listener.searchStop(fAborted);
		}
	}

	public abstract List<File> getFiles(String filePath);

	@Override
	public Object getSearchState() {
		return state;
	}

	@Override
	public void setSearchState(Object state) {
		if (state instanceof HVSCEntries)
			this.state = (HVSCEntries) state;
	}

	public SingularAttribute<?, ?> getField() {
		return field;
	}

	public void setField(SingularAttribute<?, ?> searchCriteria) {
		this.field = searchCriteria;
	}

	public Object getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(Object searchForValue) {
		this.fieldValue = searchForValue;
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}
}