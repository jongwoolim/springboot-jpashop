package me.jongwoo.jpashop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jongwoo.jpashop.domain.Member;
import me.jongwoo.jpashop.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원 가입
     */
    @Transactional
    public Long join(Member member){
        log.info("join....");
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    /**
     *  회원 조회
     */
    public Member findOne(Long memberId){
        log.info("findOne....");
        return memberRepository.findById(memberId);
    }

    /**
     * 회원 전체 조회
     */
    public List<Member> findMembers(){
        log.info("findAll....");
        return memberRepository.findAll();
    }

    private void validateDuplicateMember(Member member) {
        final List<Member> existedMember = memberRepository.findByName(member.getName());
        if(!existedMember.isEmpty())
            throw new IllegalStateException("이미 존재하는 회원입니다.");
    }
}
