package ac.kr.halford.service;

import ac.kr.halford.model.MemberModel;

public interface LoginService {
	public int join(MemberModel member);
	public MemberModel login(MemberModel member);
	public void logout ();
	
	public int findFilter ();
	public void updateFilter(int filter);
}
