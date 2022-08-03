package com.poupock.feussom.aladabusiness.util.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.poupock.feussom.aladabusiness.util.InternalPoint;
import com.poupock.feussom.aladabusiness.util.User;

public class UserInternalPtRelation {

    @Embedded
    User user;

    @Relation(
        parentColumn = "id",
        entityColumn = "user_id"
    )

    public InternalPoint internalPoint;
}
