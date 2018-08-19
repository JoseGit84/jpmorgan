import domain.Instruction;
import domain.Type;
import output.ProcessedInstructions;
import services.InstructionService;
import services.ReportService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Currency;

public class Main {
    public static void main(String args[]) {

        //Incoming instructions

        Instruction instruction1 = new Instruction();
        instruction1.setEntity("Google");
        instruction1.setType(Type.BUY);
        instruction1.setAgreedFx(new BigDecimal(1.11));
        instruction1.setCurrency(Currency.getInstance("GBP"));
        instruction1.setInstructionDate(LocalDate.of(2018, 1, 10));
        instruction1.setSettlementDate(LocalDate.of(2018, 1, 15));
        instruction1.setPricePerUnit(new BigDecimal(100));
        instruction1.setUnits(9);

        Instruction instruction2 = new Instruction();
        instruction2.setEntity("Yahoo");
        instruction2.setType(Type.BUY);
        instruction2.setAgreedFx(new BigDecimal(19.001));
        instruction2.setCurrency(Currency.getInstance("AED"));
        instruction2.setInstructionDate(LocalDate.of(2018, 1, 10));
        instruction2.setSettlementDate(LocalDate.of(2018, 6, 20));
        instruction2.setPricePerUnit(new BigDecimal(100));
        instruction2.setUnits(2);

        Instruction instruction3 = new Instruction();
        instruction3.setEntity("Asus");
        instruction3.setType(Type.BUY);
        instruction3.setAgreedFx(new BigDecimal(1.11));
        instruction3.setCurrency(Currency.getInstance("GBP"));
        instruction3.setInstructionDate(LocalDate.of(2018, 3, 12));
        instruction3.setSettlementDate(LocalDate.of(2018, 1, 15));
        instruction3.setPricePerUnit(new BigDecimal(100));
        instruction3.setUnits(10);

        //Outgoing instructions

        Instruction instruction4 = new Instruction();
        instruction4.setEntity("Lego");
        instruction4.setType(Type.SELL);
        instruction4.setAgreedFx(new BigDecimal(0.27));
        instruction4.setCurrency(Currency.getInstance("SAR"));
        instruction4.setInstructionDate(LocalDate.of(2018, 3, 12));
        instruction4.setSettlementDate(LocalDate.of(2018, 6, 10));
        instruction4.setPricePerUnit(new BigDecimal(100));
        instruction4.setUnits(5);

        Instruction instruction5 = new Instruction();
        instruction5.setEntity("Verizon");
        instruction5.setType(Type.SELL);
        instruction5.setAgreedFx(new BigDecimal(1));
        instruction5.setCurrency(Currency.getInstance("USD"));
        instruction5.setInstructionDate(LocalDate.of(2018, 5, 15));
        instruction5.setSettlementDate(LocalDate.of(2018, 6, 10));
        instruction5.setPricePerUnit(new BigDecimal(100));
        instruction5.setUnits(3);


        InstructionService instructionService = new InstructionService();
        ProcessedInstructions processedInstructions = instructionService.addInstructions(Arrays.asList(instruction1, instruction2, instruction3, instruction4, instruction5));

        ReportService.printIncomingRanking(processedInstructions);
        ReportService.printOutgoingRanking(processedInstructions);

        ReportService.printIncomingByDate(processedInstructions, LocalDate.of(2018, 6, 10));

        ReportService.printOutgoingByDate(processedInstructions, LocalDate.of(2018, 1, 15));
    }
}
