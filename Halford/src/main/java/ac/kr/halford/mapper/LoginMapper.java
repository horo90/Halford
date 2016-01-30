package ac.kr.halford.mapper;

import ac.kr.halford.model.MemberModel;

public interface LoginMapper {
	public void addMember(MemberModel member);
	public MemberModel findMember(MemberModel member);
}
