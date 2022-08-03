package com.poupock.feussom.aladabusiness.util.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.poupock.feussom.aladabusiness.util.InternalPoint;
import com.poupock.feussom.aladabusiness.util.User;

import java.util.List;

public class UserInternalPtListRelation {

    @Embedded
    User user;

    @Relation(
        parentColumn = "id",
        entityColumn = "user_id"
    )

    public List<InternalPoint> internalPoint;
}
