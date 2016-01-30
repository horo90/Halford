package ac.kr.halford.service;

import ac.kr.halford.model.MemberModel;

public interface LoginService {
	public void join(MemberModel member);
	public MemberModel login(MemberModel member);
	public void logout ();
}
