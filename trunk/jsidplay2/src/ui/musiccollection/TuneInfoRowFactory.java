package ui.musiccollection;

import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;

public class TuneInfoRowFactory implements
		Callback<TableView<TuneInfo>, TableRow<TuneInfo>> {
	@Override
	public TableRow<TuneInfo> call(final TableView<TuneInfo> p) {
		return new TableRow<TuneInfo>() {
			@Override
			public void updateItem(TuneInfo item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null) {
					setTooltip(null);
				} else {
					Tooltip tooltip = new Tooltip();
					tooltip.setText(getItem().getValue());
					setTooltip(tooltip);
				}
			}
		};
	}
}
