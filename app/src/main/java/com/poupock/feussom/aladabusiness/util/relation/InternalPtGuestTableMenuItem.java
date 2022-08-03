package com.poupock.feussom.aladabusiness.util.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.poupock.feussom.aladabusiness.util.GuestTable;
import com.poupock.feussom.aladabusiness.util.InternalPoint;

public class InternalPtGuestTableMenuItem {

    @Embedded
    InternalPoint internalPoint;

    @Relation(
        parentColumn = "id",
        entityColumn = "internal_point_id"
    )

    public GuestTable guestTable;
}
