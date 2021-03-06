// Copyright (c) 2017, Baidu.com, Inc. All Rights Reserved

// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package com.baidu.palo.common.proc;

import com.baidu.palo.catalog.Database;
import com.baidu.palo.common.AnalysisException;
import com.baidu.palo.load.ExportMgr;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

// TODO(lingbin): think if need a sub node to show unfinished instances
public class ExportProcNode implements ProcNodeInterface {
    public static final ImmutableList<String> TITLE_NAMES = new ImmutableList.Builder<String>()
            .add("JobId").add("State").add("Progress")
            .add("TaskInfo").add("ErrorMsg")
            .add("CreateTime") .add("StartTime").add("FinishTime")
            .add("Path")
            .build();

    // label and state column index of result
    public static final int LABEL_INDEX = 1;
    public static final int STATE_INDEX = 2;

    private static final int LIMIT = 2000;

    private ExportMgr exportMgr;
    private Database db;

    public ExportProcNode(ExportMgr exportMgr, Database db) {
        this.exportMgr = exportMgr;
        this.db = db;
    }

    @Override
    public ProcResult fetchResult() throws AnalysisException {
        Preconditions.checkNotNull(db);
        Preconditions.checkNotNull(exportMgr);

        BaseProcResult result = new BaseProcResult();
        result.setNames(TITLE_NAMES);

        LinkedList<List<Comparable>> jobInfos = exportMgr.getExportJobInfosByIdOrState(db.getId(), 0, null, null);
        int counter = 0;
        Iterator<List<Comparable>> iterator = jobInfos.descendingIterator();
        while (iterator.hasNext()) {
            List<Comparable> infoStr = iterator.next();
            List<String> oneInfo = new ArrayList<String>(TITLE_NAMES.size());
            for (Comparable element : infoStr) {
                oneInfo.add(element.toString());
            }
            result.addRow(oneInfo);
            if (++counter >= LIMIT) {
                break;
            }
        }
        return result;
    }

    public static int analyzeColumn(String columnName) throws AnalysisException {
        for (String title : TITLE_NAMES) {
            if (title.equalsIgnoreCase(columnName)) {
                return TITLE_NAMES.indexOf(title);
            }
        }

        throw new AnalysisException("Title name[" + columnName + "] does not exist");
    }
}
