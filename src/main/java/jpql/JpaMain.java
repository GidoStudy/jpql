package jpql;

import jakarta.persistence.*;

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
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        }finally {
            em.close();
        }
        emf.close();
    }


}
