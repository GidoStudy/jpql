package jpql;

import jakarta.persistence.*;

import java.util.Collection;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        //code
        transaction.begin();
        try {
            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            em.persist(member);

            TypedQuery<Member> selectMFromMemberM = em.createQuery("select m from Member m", Member.class);
            List<Member> m = em.createQuery("select m from Member m", Member.class)
                    .getResultList();
            // 결과가 없어도 빈 리스트를 반환하고 한 개이상일때는 그  리스트를 반환한다
            for (Member member1 : m) {
                System.out.println("member1 = " + member1);
            }
            // 결과가 정확하게 하나가 아니면 Exception 발생
            Member selectMFromMemberM1 = (Member)em.createQuery("select m from Member m")
                    .getSingleResult();
            Query query = em.createQuery("select m.username, m.age from Member m");
            // 이름 기준 파라미터 바인딩
            List<Member> members = em.createQuery("select m from Member m where m.username = :username", Member.class)
                    .setParameter("username", "member1")
                    .getResultList();
            for (Member member1 : members) {
                System.out.println("member1 = " + member1.getUsername());
            }

            // 프로젝션
            // SELECT 절에 조회할 대상을 지정하는 것
            Order order = new Order();
            Address address = new Address("Busan", "haeundae", "48091");
            member.setAddress(address);

            List<Address> resultList = em.createQuery("select m.address from Member m", Address.class)
                    .getResultList();
            for (Address o : resultList) {
                System.out.println("o = " + o);
            }
            System.out.println("============================1");
            em.flush();
            em.clear();
            System.out.println("==============================2");
            // 프로젝션 대상이 엔티티 -> 반환되는 List<Member>의 Member 는 영속성 컨텍스트에서 관리 된다
            List<Member> res = em.createQuery("select m from Member m", Member.class)
                    .getResultList();
            
            Member member1 = res.get(0);
            member1.setAge(20);

            // DTO 를 반환할 시세 패키지 경로를 다 적으면서 생성자처럼 호출 해야한다 -> 불편함
            List<MemberDTO> resultList1 = em.createQuery(
                    "select new jpql.MemberDTO(m.username, m.age) from Member m",
                            MemberDTO.class)
                    .getResultList();
            MemberDTO memberDTO = resultList1.get(0);
            System.out.println("memberDTO.getUsername() = " + memberDTO.getUsername());
            System.out.println("memberDTO.getAge() = " + memberDTO.getAge());

            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            member.setTeam(team);
            // 조인
            em.createQuery("select m from Member m join m.team t where t.name = :teamname" , Member.class)
                    .setParameter("teamname", "teamA")
                    .getResultList();
            // 세타 조인 (카티시안 프로덕트)
            em.createQuery("select m from Member m, Team t where m.username = t.name")
                            .getResultList();
            // on절을 활용한 조인
            // 조인대상 필터링, 연관관계 없는 엔티티 외부 조인
            // 회원과 팀을 조인하면서, 팀 이름이 A인 팀만 조인
            System.out.println("=======================");
            em.createQuery("select m, t from Member m left join m.team t on t.name='teamA'")
                    .getResultList();
            // 연관관계가 없는  외부 조인
            em.createQuery("select m,t from Member m left join Team t on m.username = t.name")
                    .getResultList();

            // 서브 쿼리
            em.createQuery("select m from Member m where m.age > (select avg(m2.age) from Member m2) ")
                            .getResultList();

            // exists -> 서브쿼리에 결과가 존재하면 참
            em.createQuery("select m from Member m where exists (select t from m.team t where t.name = 'teamA')")
                            .getResultList();
            // ALL 모두 만족하면 참 / ANY, SOME : 조건을 하나라도 만족하면 참
            em.createQuery("select o from Order o where o.orderAmount > all (select p.orderAmount from Product p)")
                    .getResultList();

            em.createQuery("select m from Member m where m.team = any (select t from Team t)")
                    .getResultList();

            // [NOT] IN (subquery) : 서브쿼리 결과중 하나라도 같은 것이 있으면 참

            // 경로 표현식 .을 찍어 객체 그래프를 탐색
            String query1 = "select m.username from Member m"; // 상태 필드 -> 경로 탐색의 끝 탐색 X
            String query2 = "select m.team from Member m"; // -> 단일 값 연관 : 묵시적 내부 조인 , 탐색 O
            String query3 = "select t.members from Team t"; // -> 묵시적 내부 조인 발생 X , 탐색 X

            List<Collection> resultList2 = em.createQuery(query3, Collection.class)
                    .getResultList();

            // fetch join
            // 성능 최적화를 위한 기능, 연관된 엔티티 / 컬렉션을 SQL 한 번에 함께 조회
            List<Member> resultList3 = em.createQuery("select m from Member m join fetch m.team", Member.class)
                    .getResultList();
            for (Member member2 : resultList3) {
                System.out.println("member2.getUsername() + member2.getTeam().getName() = " + member2.getUsername() + member2.getTeam().getName());
            }

            // 컬렉션 패치 조인
            List resultList4 = em.createQuery("select t from Team t join fetch t.members where t.name = 'teamA'")
                    .getResultList();


            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        }finally {
            em.close();
        }
        emf.close();
    }


}
