package jpql;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Logger;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        //code
        transaction.begin();
        try {
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
            /* 조인 inner / outer / seta : 연관관계가 없는 관계에서 조인 그냥 이어붙이기
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
            */

          /*   서브 쿼리
            // EXISTS : 서브쿼리에 결과가 존재하면 참 IN : 서브쿼리에 하나라도 만족하는 결과가 있으면 참
            // JPA는 where, having  절에서만 서브 쿼리 사용 가능 select 절은 hibernate 에서 지원
            em.createQuery("select (select avg(m1.age) from Member m1) as avg from Member m join Team t on m.userName = t.name")
                            .getResultList();
          */
            /*// ENUM TYPE 의 경우 setParameter로 쓰거나 하드 코딩시엔 패키지 경로를 적어줘야 한다
            em.createQuery("select m.userName, 'HELLO', true From Member m where m.type = :userType")
                            .setParameter("userType", MemberType.ADMIN)
                            .getResultList();
            em.createQuery("select m.userName, 'HELLO', true From Member m where m.type = :userType")
                    .getResultList();
           */
           /*  조건식 CASE식
            em.createQuery("select case when m.age <= 10 then '학생요금' " +
                                "when m.age >= 60 then '경로요금' "+
                                "else '경로요금' end "+
                    "from Member m", String.class).getResultList();
            // COALESCE: 하나씩 조회해 null이 아니면 반환

            // 사용자 이름이 없으면 이름 없는 회원 반환
            List<Member> resultList = em.createQuery("select coalesce(m.userName, '이름 없는 회원') from Member m", Member.class)
                    .getResultList();
            for (Member o : resultList) {
                System.out.println("o = " + o.toString());
            }
            // NULLIF : 두값이 같으면 null반환, 다르면 첫번째 값 반환
            // userName이 관리자면 null을 반환하고 나머지는 본인의 이름을 반환
            em.createQuery("select nullif(m.userName, '관리자') as username from Member m")
                            .getResultList();
            */
            /* JPQL 기본 함수
            em.createQuery("select concat('a','b') From Member m")
                            .getResultList();
            em.createQuery("select substring(m.userName, 2, 3) From Member m")
                    .getResultList();
            // 아래의 경우 'de'는 문자열의 4번째부터 있기 때문에 4를 반환해줌
            List<Integer> resultList = em.createQuery("select locate('de','abcdefg') From Member m", Integer.class)
                    .getResultList();
            for (Integer i : resultList) {
                System.out.println("i = " + i);
            }

            //SIZE 컬렉션의 크기에 대한 쿼리
            em.createQuery("select size(t.members) From Team t").getResultList();
            //INDEX @OrderColumn을 썼을때

            // 사용자 정의 함수 호출은 사용할 때 사용법 찾아서 하자
            */
            Team team1 = new Team();
            Team team2 = new Team();
            team1.setName("팀A");
            team2.setName("팀B");
            em.persist(team1);
            em.persist(team2);
            Member member1 = new Member();
            Member member2 = new Member();
            Member member3 = new Member();
            member1.setUserName("회원1");
            member2.setUserName("회원2");
            member3.setUserName("회원3");
            member1.setTeam(team1);
            member2.setTeam(team1);
            member3.setTeam(team2);
            em.persist(member1);
            em.persist(member2);
            em.persist(member3);

            em.flush();
            em.clear();
            // 경로 표현식 : .을 찍어 객체 그래프를 탐색하는 것
            // fetch join ******** 엄청 중요함
            // sql 조인 종류X , JPQL에서 성능 최적화를 위해 제공하는 기능
            // 연관 엔티티나 컬렉션을 SQL 한번에 함께 조회하는 기능
            // 회원1. 팀A -> SQL
            // 회원2. 팀A -> 1차캐시
            // 회원3. 팀B -> SQL N+1 문제 발생
            //em.createQuery("select m from Member  m").getResultList();
            // select 문이 한번만 나감 / Team도 프록시가 아닌 엔티티를 가져옴
            List<Member> resultList = em.createQuery("select m from Member m join fetch m.team", Member.class)
                    .getResultList();
            for (Member m : resultList) {
                System.out.println("m = " + m);
                System.out.println("m.getTeam() = " + m.getTeam().getName());
            }
            // join시 on은 해당 관계에 대해서 join이 일어나고 where 은 조인이 일어난 뒤에 필터링을 한다
            // 컬렉션 패치 조인 -> 일대다 관계 OneTOMany
            // fetch join 을하면 즉시로딩 처럼 동작하게 되는데 LAZY + fetch join으로
            // 필요할때만 연관된 엔티티까지 모두 가져와 리소스 낭비를 줄이며 성능 튜닝이 가능해진다.
            List<Team> resultList1 = em.createQuery("select t From Team t join fetch t.members", Team.class)
                    .getResultList();
            for (Team t : resultList1) {
                System.out.println("t.getName() = " + t.getName() + " | members = " + t.getMembers().size());
                for (Member tMember : t.getMembers()) {
                    System.out.println("-> member = " + tMember);
                }
            }
            // 한계 - 패치 조인의 대상에는 별칭을 부여할 수가 없다, 둘 이상의 컬렉션은패치 조인 할 수 없다,
            // 컬렉션을 패치조인하면 페이징API를 사용할 수 없음.일대일, 다대일 값 연관 필드는 패치조인해도 가능은 한데 위험함
            // 위험성 패치 조인을 통해 team 조회 -> member 5명 중 3명이 같이 딸려왔다 ->
            // 딸려온 3명만 로직이 굴러갈 수 있고 cascade 등의 생명주기도 엮여 있으면 사고난다
            em.createQuery("select m from Member m join fetch m.team", Team.class)
                            .setFirstResult(0)
                            .setMaxResults(1)
                            .getResultList();
            // 필요하다면 이렇게 다대일로 fetch join 하고 페이징 처리 해야함
            System.out.println("===============");
            // BatchSize를 설정해줘서 쿼리를 in으로 여러개 쏴버리는 방법도 있다.
            List<Team> resultList2 = em.createQuery("select t from Team t join fetch t.members", Team.class)
                    .setFirstResult(0)
                    .setMaxResults(2)
                    .getResultList();
            for (Team team3 : resultList2) {
                System.out.println("t.getName() = " + team3.getName() + " | members = " + team3.getMembers().size());
                for (Member tMember : team3.getMembers()) {
                    System.out.println("-> member = " + tMember);
                }
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }

}
