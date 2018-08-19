package services;

import com.google.common.collect.TreeMultiset;
import domain.Instruction;
import domain.Type;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import output.ProcessedInstructions;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.SortedMap;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class InstructionServiceTest {

    private static final LocalDate JUL_20 = LocalDate.of(2018, 7, 20);
    private static final LocalDate JUL_21 = LocalDate.of(2018, 7, 21);
    private static final LocalDate JUL_22 = LocalDate.of(2018, 7, 22);
    private static final LocalDate JUL_23 = LocalDate.of(2018, 7, 23);
    private static final LocalDate JUL_24 = LocalDate.of(2018, 7, 24);

    private Instruction instruction1;

    private Instruction instruction2;

    private Instruction instruction3;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    private InstructionService instructionService = new InstructionService();

    @Before
    public void setUp() {
        instruction1 = new Instruction();
        instruction1.setEntity("Nikon");
        instruction1.setType(Type.BUY);
        instruction1.setAgreedFx(new BigDecimal(1.11));
        instruction1.setCurrency(Currency.getInstance("GBP"));
        instruction1.setInstructionDate(JUL_21);
        instruction1.setPricePerUnit(new BigDecimal(100));
        instruction1.setUnits(3);

        instruction2 = new Instruction();
        instruction2.setEntity("Canon");
        instruction2.setType(Type.BUY);
        instruction2.setAgreedFx(new BigDecimal(0.87));
        instruction2.setCurrency(Currency.getInstance("EUR"));
        instruction2.setInstructionDate(JUL_22);
        instruction2.setPricePerUnit(new BigDecimal(100));
        instruction2.setUnits(2);

        instruction3 = new Instruction();
        instruction3.setEntity("Fujifilm");
        instruction3.setType(Type.BUY);
        instruction3.setAgreedFx(new BigDecimal(0.0079));
        instruction3.setCurrency(Currency.getInstance("JPY"));
        instruction3.setInstructionDate(JUL_23);
        instruction3.setPricePerUnit(new BigDecimal(100));
        instruction3.setUnits(1);
    }

    @Test
    public void testAddInstructions_WithNull() {
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Please provide a valid list of instructions");
        instructionService.addInstructions(null);
    }

    @Test
    public void testAddInstructions_WithSettlementOnWeekend_And_WesternCurrency() {
        //Saturday
        instruction1.setSettlementDate(JUL_21);
        ProcessedInstructions processedInstructions = instructionService.addInstructions(Collections.singletonList(instruction1));

        //Check number of instructions added
        TreeMultiset<Instruction> outcomingInstructions = processedInstructions.getSortedOutgoingInstructions();
        Instruction[] instructions = outcomingInstructions.toArray(new Instruction[outcomingInstructions.size()]);
        assertArrayEquals(new Instruction[]{instruction1}, instructions);

        //Check settlement date has changed to the next Monday
        assertEquals(JUL_23, instructions[0].getSettlementDate());
        assertEquals(DayOfWeek.MONDAY, instructions[0].getSettlementDate().getDayOfWeek());
    }

    @Test
    public void testAddInstructions_WithSettlementOnWeekend_And_MiddleEastCurrency() {
        instruction1.setCurrency(Currency.getInstance("AED"));
        //Fridays
        instruction1.setSettlementDate(JUL_20);
        ProcessedInstructions processedInstructions = instructionService.addInstructions(Collections.singletonList(instruction1));

        //Check number of instructions added
        TreeMultiset<Instruction> outcomingInstructions = processedInstructions.getSortedOutgoingInstructions();
        Instruction[] instructions = outcomingInstructions.toArray(new Instruction[outcomingInstructions.size()]);
        assertArrayEquals(new Instruction[]{instruction1}, instructions);

        //Check settlement date has changed to the next Sunday
        assertEquals(JUL_22, instructions[0].getSettlementDate());
        assertEquals(DayOfWeek.SUNDAY, instructions[0].getSettlementDate().getDayOfWeek());
    }

    @Test
    public void testAddInstructions_WithSettlementOnWorkingDate_And_WesternCurrency() {
        //Monday
        instruction1.setSettlementDate(InstructionServiceTest.JUL_23);
        ProcessedInstructions processedInstructions = instructionService.addInstructions(Collections.singletonList(instruction1));

        //Check number of instructions added
        TreeMultiset<Instruction> outcomingInstructions = processedInstructions.getSortedOutgoingInstructions();
        Instruction[] instructions = outcomingInstructions.toArray(new Instruction[outcomingInstructions.size()]);
        assertArrayEquals(new Instruction[]{instruction1}, instructions);

        //Check settlement date has not changed
        assertEquals(JUL_23, instructions[0].getSettlementDate());
    }

    @Test
    public void testAddInstructions_WithSettlementOnWorkingDate_And_MiddleEastCurrency() {

        instruction1.setCurrency(Currency.getInstance("AED"));

        //Sunday
        instruction1.setSettlementDate(JUL_22);
        ProcessedInstructions processedInstructions = instructionService.addInstructions(Collections.singletonList(instruction1));

        //Check number of instructions added
        TreeMultiset<Instruction> outcomingInstructions = processedInstructions.getSortedOutgoingInstructions();
        Instruction[] instructions = outcomingInstructions.toArray(new Instruction[outcomingInstructions.size()]);
        assertArrayEquals(new Instruction[]{instruction1}, instructions);

        //Check settlement date has not changed
        assertEquals(JUL_22, instructions[0].getSettlementDate());
    }

    @Test
    public void testAddInstructions_AreOrderedByUsdAmount_In_Outcoming() {
        instruction3.setSettlementDate(JUL_23);
        instruction2.setSettlementDate(JUL_24);
        instruction1.setSettlementDate(JUL_24);

        ProcessedInstructions processedInstructions = instructionService.addInstructions(Arrays.asList(instruction3, instruction2, instruction1));

        TreeMultiset<Instruction> incomingInstructions = processedInstructions.getSortedIncomingInstructions();
        TreeMultiset<Instruction> outcomingInstructions = processedInstructions.getSortedOutgoingInstructions();

        //Check there's none incoming instructions
        Assert.assertTrue(incomingInstructions.isEmpty());

        //Check they are ordered by amount in USD
        Instruction[] instructions = outcomingInstructions.toArray(new Instruction[outcomingInstructions.size()]);
        assertArrayEquals(new Instruction[]{instruction1, instruction2, instruction3}, instructions);
    }

    @Test
    public void testAddInstructions_AreOrderedByUsdAmount_In_Incoming() {
        instruction3.setType(Type.SELL);
        instruction3.setSettlementDate(JUL_23);
        instruction2.setType(Type.SELL);
        instruction2.setSettlementDate(JUL_24);
        instruction1.setType(Type.SELL);
        instruction1.setSettlementDate(JUL_24);

        ProcessedInstructions processedInstructions = instructionService.addInstructions(Arrays.asList(instruction3, instruction2, instruction1));

        TreeMultiset<Instruction> incomingInstructions = processedInstructions.getSortedIncomingInstructions();
        TreeMultiset<Instruction> outcomingInstructions = processedInstructions.getSortedOutgoingInstructions();

        //Check there's none outcoming instructions
        Assert.assertTrue(outcomingInstructions.isEmpty());

        //Check they are ordered by amount in USD
        Instruction[] instructions = incomingInstructions.toArray(new Instruction[incomingInstructions.size()]);
        assertArrayEquals(new Instruction[]{instruction1, instruction2, instruction3}, instructions);
    }

    @Test
    public void testAddInstructions_AccumulatedIncomingAmountInUsdPerDate() {
        instruction1.setType(Type.SELL);
        instruction1.setSettlementDate(JUL_23);
        instruction2.setType(Type.SELL);
        instruction2.setSettlementDate(JUL_24);
        instruction3.setType(Type.SELL);
        instruction3.setSettlementDate(JUL_24);

        ProcessedInstructions processedInstructions = instructionService.addInstructions(Arrays.asList(instruction1, instruction2, instruction3));

        //Check there is only 2 entries in the incomings map
        SortedMap<LocalDate, BigDecimal> incomingAmountPerDateMap = processedInstructions.getUsdIncomingAmountPerDate();
        assertEquals(2, incomingAmountPerDateMap.keySet().size());

        //May 4th
        BigDecimal actualAmountMayForth = incomingAmountPerDateMap.get(JUL_23);
        BigDecimal amountInUsdInstruction1 = InstructionService.getAmountInUSD(instruction1);
        assertEquals(amountInUsdInstruction1, actualAmountMayForth);

        //May 7th
        BigDecimal actualAmountMaySeventh = incomingAmountPerDateMap.get(JUL_24);
        BigDecimal amountInUsdInstruction2 = InstructionService.getAmountInUSD(instruction2);
        BigDecimal amountInUsdInstruction3 = InstructionService.getAmountInUSD(instruction3);
        assertEquals(amountInUsdInstruction2.add(amountInUsdInstruction3), actualAmountMaySeventh);
    }

    @Test
    public void testAddInstructions_AccumulatedOutgoingAmountInUsdPerDate() {
        instruction1.setSettlementDate(JUL_23);
        instruction2.setSettlementDate(JUL_24);
        instruction3.setSettlementDate(JUL_24);

        ProcessedInstructions processedInstructions = instructionService.addInstructions(Arrays.asList(instruction1, instruction2, instruction3));

        //Check there is only 2 entries in the incomings map
        SortedMap<LocalDate, BigDecimal> outgoingAmountPerDateMap = processedInstructions.getUsdOutgoingAmountPerDate();
        assertEquals(2, outgoingAmountPerDateMap.keySet().size());

        //Apr 3rd
        BigDecimal actualAmountAprThird = outgoingAmountPerDateMap.get(JUL_23);
        BigDecimal amountInUsdInstruction1 = InstructionService.getAmountInUSD(instruction1);
        assertEquals(amountInUsdInstruction1, actualAmountAprThird);

        //Apr 4th
        BigDecimal actualAmountAprForth = outgoingAmountPerDateMap.get(JUL_24);
        BigDecimal amountInUsdInstruction2 = InstructionService.getAmountInUSD(instruction2);
        BigDecimal amountInUsdInstruction3 = InstructionService.getAmountInUSD(instruction3);
        assertEquals(amountInUsdInstruction2.add(amountInUsdInstruction3), actualAmountAprForth);
    }
}