import com.snowflake.snowpark_java.DataFrame;
import com.snowflake.snowpark_java.Row;
import com.snowflake.snowpark_java.Session;
import com.snowflake.snowpark_java.types.StructType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class HelloSnowpark {
    public static void main(String[] args) {
        // Replace the <placeholders> below.
        Map<String, String> properties = new HashMap<>();
        properties.put("URL", "https://VHHMHHJ-CX23460.snowflakecomputing.com:443");
        properties.put("USER", "WEILIU");
        properties.put("PASSWORD", "hcJ7bExXFypKCHP");
        properties.put("ROLE", "ACCOUNTADMIN");
        properties.put("WAREHOUSE", "COMPUTE_WH");
        properties.put("DB", "SNOWFLAKE_SAMPLE_DATA");
        properties.put("SCHEMA", "TPCH_SF1");
        Session session = Session.builder().configs(properties).create();
//        session.sql("show tables").show();
//        "created_on"             |"name"    |"database_name"        |"schema_name"  |"kind"  |"comment"                          |"cluster_by"  |"rows"   |"bytes"    |"owner"  |"retention_time"  |"automatic_clustering"  |"change_tracking"  |"search_optimization"  |"search_optimization_progress"  |"search_optimization_bytes"  |"is_external"  |"enable_schema_evolution"  |"owner_role_type"  |"is_event"  |"is_hybrid"  |"is_iceberg"  |"is_dynamic"  |"is_immutable"  |
//        ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
//        |2021-11-11 02:04:31.283  |CUSTOMER  |SNOWFLAKE_SAMPLE_DATA  |TPCH_SF1       |TABLE   |Customer data as defined by TPC-H  |              |150000   |10747904   |         |1                 |OFF                     |OFF                |OFF                    |NULL                            |NULL                         |N              |N                          |                   |N           |N            |N             |N             |N               |
//        |2021-11-11 02:04:31.672  |LINEITEM  |SNOWFLAKE_SAMPLE_DATA  |TPCH_SF1       |TABLE   |Lineitem data as defined by TPC-H  |              |6001215  |165228544  |         |1                 |OFF                     |OFF                |OFF                    |NULL                            |NULL                         |N              |N                          |                   |N           |N            |N             |N             |N               |
//        |2021-11-11 02:04:31.624  |NATION    |SNOWFLAKE_SAMPLE_DATA  |TPCH_SF1       |TABLE   |Nation data as defined by TPC-H    |              |25       |4096       |         |1                 |OFF                     |OFF                |OFF                    |NULL                            |NULL                         |N              |N                          |                   |N           |N            |N             |N             |N               |
//        |2021-11-11 02:04:31.628  |ORDERS    |SNOWFLAKE_SAMPLE_DATA  |TPCH_SF1       |TABLE   |Orders data as defined by TPC-H    |              |1500000  |42303488   |         |1                 |OFF                     |OFF                |OFF                    |NULL                            |NULL                         |N              |N                          |                   |N           |N            |N             |N             |N               |
//        |2021-11-11 02:04:31.596  |PART      |SNOWFLAKE_SAMPLE_DATA  |TPCH_SF1       |TABLE   |Part data as defined by TPC-H      |              |200000   |5214208    |         |1                 |OFF                     |OFF                |OFF                    |NULL                            |NULL                         |N              |N                          |                   |N           |N            |N             |N             |N               |
//        |2021-11-11 02:04:31.603  |PARTSUPP  |SNOWFLAKE_SAMPLE_DATA  |TPCH_SF1       |TABLE   |Partsupp data as defined by TPC-H  |              |800000   |36589568   |         |1                 |OFF                     |OFF                |OFF                    |NULL                            |NULL                         |N              |N                          |                   |N           |N            |N             |N             |N               |
//        |2021-11-11 02:04:32.785  |REGION    |SNOWFLAKE_SAMPLE_DATA  |TPCH_SF1       |TABLE   |Region data as defined by TPC-H    |              |5        |4096       |         |1                 |OFF                     |OFF                |OFF                    |NULL                            |NULL                         |N              |N                          |                   |N           |N            |N             |N             |N               |
//        |2021-11-11 02:04:32.765  |SUPPLIER  |SNOWFLAKE_SAMPLE_DATA  |TPCH_SF1       |TABLE   |Supplier data as defined by TPC-H  |              |10000    |692224     |         |1                 |OFF                     |OFF                |OFF                    |NULL                            |NULL                         |N              |N                          |                   |N           |N            |N             |N             |N               |
        StructType schema = session.sql("select * from CUSTOMER").schema();
        Row[] data = new Row[]{new Row(new Object[]{100001L, "Customer#0000100001", "Ii4zQn9cX", 111L, "13476241939", new BigDecimal("99999.10"), "HOUSEHOLD", "test insert data frame"})};
            DataFrame dataFrame = session.createDataFrame(data, schema);
        dataFrame.write().mode("append").saveAsTable("CUSTOMER");
        session.sql("select * from CUSTOMER where C_CUSTKEY='100001'").show();

    }
}