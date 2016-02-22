package ac.kr.halford.dbtemplate;

import ac.kr.halford.model.MemberModel;

public interface LoginDAO {
	public int addMember(MemberModel member);
	public MemberModel findMember(MemberModel member);
	public int findFilter();
	public void updateFilter(int filter);
}
