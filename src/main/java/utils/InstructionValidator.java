package utils;

import domain.Instruction;

public class InstructionValidator {

    public boolean isValid(Instruction instruction) {
        return isValidEntityType(instruction) && isValidDates(instruction) && isValidPriceFx(instruction);
    }

    private boolean isValidEntityType(Instruction instruction) {
        return instruction.getEntity() != null && instruction.getType() != null;
    }

    private boolean isValidPriceFx(Instruction instruction) {
        return instruction.getAgreedFx() != null && instruction.getPricePerUnit() != null && instruction.getCurrency() != null;
    }

    private boolean isValidDates(Instruction instruction) {
        return instruction.getInstructionDate() != null && instruction.getSettlementDate() != null;
    }
}
