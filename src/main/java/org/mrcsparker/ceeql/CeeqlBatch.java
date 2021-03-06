package org.mrcsparker.ceeql;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mrcsparker.ceeql.CeeqlTemplate.Batch;
//import org.skife.jdbi.v2.Batch;
import org.skife.jdbi.v2.Handle;
//import org.skife.jdbi.v2.PreparedBatch;

import java.io.IOException;
import java.util.Map;

class CeeqlBatch {

    private final static Logger log = LogManager.getLogger(CeeqlBatch.class);

    private final Handle dbiHandle;
    private final String sql;
    private final Map<String, String> args;

    public CeeqlBatch(Handle dbiHandle, String sql, Map<String, String> args) {
        this.dbiHandle = dbiHandle;
        this.sql = sql;
        this.args = args;
    }

    public String exec() throws IOException {
        Batch batch = CeeqlTemplate.apply(sql, args, dbiHandle);
        
        //TODO: not needed for PreparedBatch?
        for (Map.Entry<String, String> arg : args.entrySet()) {
            batch.define(arg.getKey(), arg.getValue());
        }

        try {
            return CeeqlJson.generate(batch.execute());
        } catch (Exception e) {
            return CeeqlError.errorType(e.getClass().getSimpleName(), e.getMessage());
        }
    }
}
