package me.jack.lat.lmsbackendmongo.entities;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import org.bson.types.ObjectId;

import java.util.Date;

@Entity("loanedBooks")
public class LoanedBook {

    @Id
    private ObjectId loanedBookId;

    @Reference
    private Book book;
    @Reference
    private User user;

    private Date loanedAt;
    private Date returnedAt;

    private FinePaid finePaid;

    public LoanedBook() {
        this.loanedAt = new Date();
    }

    public LoanedBook(Book book, User user) {
        this.book = book;
        this.user = user;
        this.loanedAt = new Date();
    }

    public String getLoanedBookId() {
        return loanedBookId.toString();
    }

    public void setLoanedBookId(ObjectId loanedBookId) {
        this.loanedBookId = loanedBookId;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getLoanedAt() {
        return loanedAt;
    }

    public void setLoanedAt(Date loanedAt) {
        this.loanedAt = loanedAt;
    }

    public Date getReturnedAt() {
        return returnedAt;
    }

    public void setReturnedAt(Date returnedAt) {
        this.returnedAt = returnedAt;
    }

    public FinePaid getFinePaid() {
        return finePaid;
    }

    public void setFinePaid(FinePaid finePaid) {
        this.finePaid = finePaid;
    }

    @Entity("finePaid")
    public static class FinePaid {
        private Date paidAt;
        private double amountPaid;

        public FinePaid() {
            this.paidAt = new Date();
        }

        public FinePaid(double amountPaid) {
            this.amountPaid = amountPaid;
            this.paidAt = new Date();
        }

        public Date getPaidAt() {
            return paidAt;
        }

        public void setPaidAt(Date paidAt) {
            this.paidAt = paidAt;
        }

        public double getAmountPaid() {
            return amountPaid;
        }

        public void setAmountPaid(double amountPaid) {
            this.amountPaid = amountPaid;
        }
    }
}
