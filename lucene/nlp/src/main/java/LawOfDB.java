/**
 * Created by Rocky on 2018/1/22.
 */
public class LawOfDB {
    String lname;

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    Oid _id;
    public class Oid{
        String $oid;
        public String get$oid() {
            return $oid;
        }

        public void set$oid(String $oid) {
            this.$oid = $oid;
        }

    }
}
