package com.retro.tragaperras;

public final class SlotSpinResult
{
    public final int itemId;
    public final int balanceBefore;
    public final int balanceAfter;
    public final int normalPrize;
    public final long jackpotPrize;
    public final boolean jackpotHit;
    public final long jackpotAfter;
    public final long treasuryAfter;
    public final int symbol1;
    public final int symbol2;
    public final int symbol3;

    public SlotSpinResult(
            int itemId,
            int balanceBefore,
            int balanceAfter,
            int normalPrize,
            long jackpotPrize,
            boolean jackpotHit,
            long jackpotAfter,
            long treasuryAfter,
            int symbol1,
            int symbol2,
            int symbol3)
    {
        this.itemId = itemId;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        this.normalPrize = normalPrize;
        this.jackpotPrize = jackpotPrize;
        this.jackpotHit = jackpotHit;
        this.jackpotAfter = jackpotAfter;
        this.treasuryAfter = treasuryAfter;
        this.symbol1 = symbol1;
        this.symbol2 = symbol2;
        this.symbol3 = symbol3;
    }
}