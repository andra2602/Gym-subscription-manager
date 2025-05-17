package tests;

import dao.MemberDAO;
import models.Member;
import models.Trainer;

import java.time.LocalDate;
import java.util.List;

public class TestMemberDAO {
    public static void main(String[] args) {
        MemberDAO memberDAO = new MemberDAO();

        // 1. Creare membru
        Member member = new Member(
                "Ana Ionescu",
                "anaionescu",
                "ana.ionescu@example.com",
                "0733445566",
                "Parola123!",
                LocalDate.now(),
                60f,
                1.65f,
                "beginner",
                null,
                null,
                false
        );

        memberDAO.create(member);
        System.out.println("Membru creat cu ID-ul: " + member.getId());

        // 2. Citire toti membrii
        List<Member> members = memberDAO.readAll();
        System.out.println("Lista membri:");
        for (Member m : members) {
            System.out.printf("ID: %d, Nume: %s, Trainer: %s\n", m.getId(), m.getName(),
                    m.getTrainer() != null ? m.getTrainer().getName() : "N/A");
        }

        // 3. Citire membru dupa ID
        Member memberById = memberDAO.readById(member.getId());
        if (memberById != null) {
            System.out.println("Membru gasit dupa ID: " + memberById.getName());
        } else {
            System.out.println("Membrul nu a fost gasit.");
        }

        // 4. Actualizare trainer membru
        Trainer trainer = new Trainer();
        trainer.setId(1);  // presupunem ca exista un trainer cu id 1
        boolean updated = memberDAO.updateTrainer(member.getId(), trainer.getId());
        System.out.println("Actualizare trainer: " + (updated ? "Succes" : "Esuat"));

        // 5. Stergere membru
        // boolean deleted = memberDAO.delete(member.getId());
        // System.out.println("Stergere membru: " + (deleted ? "Succes" : "Esuat"));
    }
}
