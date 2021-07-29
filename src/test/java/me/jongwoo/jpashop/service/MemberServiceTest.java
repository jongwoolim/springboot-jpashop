package me.jongwoo.jpashop.service;

import me.jongwoo.jpashop.domain.Member;
import me.jongwoo.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EntityManager em;

    @Test
//    @Rollback(false)
    public void 회원가입() throws Exception{
        //Given
         Member member = new Member();
         member.setName("Lim");
        //When
        final Long savedId = memberService.join(member);
        //Then
        em.flush();
        final Member savedMember = memberRepository.findById(savedId);
        assertThat(member.getName()).isEqualTo(savedMember.getName());
    }

    @Test
    public void 중복_회원_예외() throws Exception{
        //Given
        Member member1 = new Member();
        member1.setName("Lim1");

        Member member2 = new Member();
        member2.setName("Lim1");

        //When
        memberService.join(member1);
//        try{
//            memberService.join(member2);
//        }catch (IllegalStateException ex){
//            return;
//        }
        //Then
        assertThrows(IllegalStateException.class, () -> {
            memberService.join(member2);
        });
//        fail("예외가 발생해야 함.");

    }
}