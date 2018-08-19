package domain;

import services.InstructionService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Objects;

public class Instruction {

    private String entity;
    private Type type;
    private BigDecimal agreedFx;
    private Currency currency;
    private LocalDate instructionDate;
    private LocalDate settlementDate;
    private int units;
    private BigDecimal pricePerUnit;

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public BigDecimal getAgreedFx() {
        return agreedFx;
    }

    public void setAgreedFx(BigDecimal agreedFx) {
        this.agreedFx = agreedFx;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public LocalDate getInstructionDate() {
        return instructionDate;
    }

    public void setInstructionDate(LocalDate instructionDate) {
        this.instructionDate = instructionDate;
    }

    public LocalDate getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(LocalDate settlementDate) {
        this.settlementDate = settlementDate;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Instruction that = (Instruction) o;
        return InstructionService.getAmountInUSD(this).equals(InstructionService.getAmountInUSD(that));
    }

    @Override
    public int hashCode() {
        return Objects.hash(agreedFx, units, pricePerUnit);
    }
}
