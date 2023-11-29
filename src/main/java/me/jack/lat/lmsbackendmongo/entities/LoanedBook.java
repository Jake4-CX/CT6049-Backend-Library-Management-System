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

    private LoanFine loanFine;

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

    public LoanFine getLoanFine() {
        return loanFine;
    }

    public void setLoanFine(LoanFine loanFine) {
        this.loanFine = loanFine;
    }

    @Entity("loanFine")
    public static class LoanFine {
        private Date paidAt;
        private double fineAmount;

        public LoanFine() {
        }

        public LoanFine(double fineAmount) {
            this.fineAmount = fineAmount;
        }

        public LoanFine(double fineAmount, Date paidAt) {
            this.fineAmount = fineAmount;
            this.paidAt = paidAt;
        }

        public Date getPaidAt() {
            return paidAt;
        }

        public void setPaidAt(Date paidAt) {
            this.paidAt = paidAt;
        }

        public double getFineAmount() {
            return fineAmount;
        }

        public void setFineAmount(double fineAmount) {
            this.fineAmount = fineAmount;
        }
    }
}
