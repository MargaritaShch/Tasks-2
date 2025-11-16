package pref;

import io.gatling.javaapi.core.ChainBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;

public class Chains {

    public static final ChainBuilder getProduct =
            exec(Actions.getById());

    public static final ChainBuilder updateProduct =
            exec(Actions.update());

    public static final ChainBuilder createProduct =
            exec(Actions.create());

    public static final ChainBuilder deleteProduct =
            exec(Actions.deleteById());
}
