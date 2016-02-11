package ac.kr.halford.mapper;

import ac.kr.halford.model.MemberModel;

public interface LoginDAO {
	public void addMember(MemberModel member);
	public MemberModel findMember(MemberModel member);
}
