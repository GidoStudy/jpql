package jpql;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.logging.Logger;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        //code
        transaction.begin();
        try{
            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUserName("member1");
            member.setTeam(team);
            member.setAge(10);


            em.persist(member);
             /* 기본 문법과 쿼리 API
            // TypeQuery, Query
            // TypedQuery<Member> query = em.createQuery("select m from Member m", Member.class);
            // Query query2 = em.createQuery("select m from Member m");
            TypedQuery<Member> query = em.createQuery("select m from Member m where m.userName = :userName", Member.class);
            // .setParameter("userName", "member1").getResultList(); 보통은 체이닝 기법으로 이용
            // 결과값이 컬렉션일때와 하나만 받을 때
            // singleResult 는 결과가 없거나 2개 이상이면 Exception 터진다
            Member singleResult = query.getSingleResult();
            System.out.println("singleResult = " + singleResult);
            List<Member> resultList = query.getResultList();*/


            em.flush();
            em.clear();
        /* 프로젝션
            // 프로젝션 select 절에 조회할 대상 지정
            // jpql로 가져온 엔티티들 -> 바로 영속성 컨텍스트에서 관리된다
            List<Address> resultList = em.createQuery("select o.address from Orders o", Address.class)
                    .getResultList();
//            Member member1 = resultList.get(0);
//            member1.setAge(20);
            // 프로젝션 - 여러 값 조회 Query 로 되고 Object 타입으로 준다 캐스팅해야함
           // Query query = em.createQuery("select m.userName, m.age from Member m");
            List result = em.createQuery("select m.userName, m.age from Member m").getResultList();
            Object o = result.get(0);
            // new 명령어 조회(패키지명 적어줘야 함. 안이 String이라 어쩔 수 없다)
            List<MemberDTO> list = em.createQuery("select new jpql.MemberDTO(m.userName, m.age) from Member m", MemberDTO.class)
                    .getResultList();
         */
            /*
            for (int i = 0; i < 100; i++) {
                Member member2 = new Member();
                member2.setUserName("member"+i);
                member2.setAge(i);
                em.persist(member2);
            }
            // 페이징 API setFireResult와 maxResult로 바로 페이징 처리
            List<Member> result = em.createQuery("select m from Member m order by m.age desc", Member.class)
                    .setFirstResult(0)
                    .setMaxResults(10)
                    .getResultList();
            System.out.println("result = " + result.size());
            for (Member member1 : result) {
                System.out.println("member = " + member1.toString());
            }
*/
            // 조인 inner / outer / seta : 연관관계가 없는 관계에서 조인 그냥 이어붙이기
            String query = "select m from Member m join m.team";
            List<Member> result = em.createQuery(query, Member.class).getResultList();
            for (Member member1 : result) {
                System.out.println("member1 = " + member1);
            }
            //seta join
            List<Member> resultList = em.createQuery("select m from Member m, Team t where m.userName = t.name", Member.class)
                    .getResultList();
            // join 대상 필터링 on
            List<Member> list = em.createQuery("select m from Member m Left Join m.team t on t.name='teamA'", Member.class)
                    .getResultList();
            for (Member member1 : list) {
                System.out.println("member1.getUserName() = " + member1.getUserName());
                System.out.println("member1.getTeam().getName() = " + member1.getTeam().getName());
            }
            // 연관관계가 전혀 없는 엔티티 외부 조인
            em.createQuery("select m,t from Member m Left join Team t on m.userName = t.name")
                            .getResultList();
            transaction.commit();
        }catch (Exception e){
            transaction.rollback();
        }finally {
            em.close();
        }
        emf.close();
    }

}
