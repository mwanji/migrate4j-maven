package db.migrations;

import com.eroi.migrate.Execute;
import com.eroi.migrate.Migration;

public class Migration_7 implements Migration {

	public static final String NEW_COLUMN_NAME = "progress";
	public static final String OLD_COLUMN_NAME = Migration_2.STATUS_COLUMN_NAME;
	public static final String TABLE_NAME = Migration_1.TABLE_NAME;
	
	public void down() {
		Execute.renameColumn(OLD_COLUMN_NAME, NEW_COLUMN_NAME, TABLE_NAME);
	}

	public void up() {
		Execute.renameColumn(NEW_COLUMN_NAME, OLD_COLUMN_NAME, TABLE_NAME);
	}

}
