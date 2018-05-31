package org.nd4j.linalg.api.ops.impl.scatter;

import org.nd4j.autodiff.samediff.SDVariable;
import org.nd4j.autodiff.samediff.SameDiff;
import org.nd4j.imports.NoOpNameFoundException;
import org.nd4j.linalg.api.ops.DynamicCustomOp;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by farizrahman4u on 3/23/18.
 */

public class ScatterAdd extends DynamicCustomOp {

    public ScatterAdd(SameDiff sameDiff, SDVariable ref, SDVariable indices, SDVariable updates) {
        super(null, sameDiff, new SDVariable[]{ref, indices, updates}, false);
    }

    public ScatterAdd(){}

    @Override
    public String opName() {
        return "scatter_add";
    }

    @Override
    public String onnxName() {
        throw new NoOpNameFoundException("No onnx op opName found for " + opName());
    }

    @Override
    public String tensorflowName() {
        return "ScatterAdd";
    }

    @Override
    public List<SDVariable> doDiff(List<SDVariable> gradOut){
        //3 args: ref, indices, updates
        //For non-modified indices, input gradient (referenc) is same as output gradient
        //For modified indices, dL/dref = dL/dOut * dOut/dRef = dL/dOut * d(ref + update)/dRef = dL/dOut
        //And for updates, dL/du = dL/dOut * dOut/du = dL/dOut * d(ref + update)/du = dL/dOut -> gather op

        List<SDVariable> ret = new ArrayList<>(3);
        ret.add(gradOut.get(0));            //Reference array
        ret.add(f().zerosLike(args()[1]));  //Indices

        SDVariable gather = f().gather(gradOut.get(0), args()[1], 0);       //Updates
        ret.add(gather);

        return ret;
    }

}
