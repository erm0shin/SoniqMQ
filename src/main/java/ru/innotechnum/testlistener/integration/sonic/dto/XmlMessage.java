package ru.innotechnum.testlistener.integration.sonic.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.Objects;

@XmlRootElement(name = "account")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlMessage {

    @XmlElement(required = true)
    private String name;

    @XmlElement
    private String description;

    @XmlElement
    private BigDecimal balance;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XmlMessage message = (XmlMessage) o;
        return Objects.equals(name, message.name) &&
                Objects.equals(description, message.description) &&
                Objects.equals(balance, message.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, balance);
    }

    @Override
    public String toString() {
        return "Account [name=" + name + ", description=" + description
                + ", balance=" + balance + "]";
    }

}
