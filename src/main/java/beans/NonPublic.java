
package beans;

import javax.annotation.processing.Generated;

@Generated("jsonschema2pojo")
public class NonPublic {


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(NonPublic.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof NonPublic) == false) {
            return false;
        }
        NonPublic rhs = ((NonPublic) other);
        return true;
    }

}
