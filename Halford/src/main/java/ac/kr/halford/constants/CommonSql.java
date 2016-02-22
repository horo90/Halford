package ac.kr.halford.constants;

public class CommonSql {
	public static final String findFilter = "SELECT filter FROM filter WHERE filter in (0, 1, 2)";
	public static final String updateFilter = "UPDATE filter SET filter=? WHERE filter in (0, 1, 2)";
}
