package mapping;

import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Parameter;
import de.bwaldvogel.liblinear.Problem;
import de.bwaldvogel.liblinear.SolverType;
import java.io.File;
import supervised.evaluation.FoldCreator;
import supervised.evaluation.FoldSplitter;

/**
 * This class trains the learner
 */
public class Trainer {

    public static void main(String[] args) throws Exception {
        FoldSplitter.split(1);
        FoldCreator.create(1);
        
        SolverType solver = SolverType.L2R_L2LOSS_SVC_DUAL;
        int bias = 1;
        double C = 0.125;   // cost of constraints violation
        double eps = 0.3;   // stopping criteria
        Parameter parameter = new Parameter(solver, C, eps);
        
        File file = new File("text_files\\supervised\\rankFolds\\fold1");
        Problem problem = Problem.readFromFile(file, bias);
        Model model = Linear.train(problem, parameter);
        File modelFile = new File("text_files\\supervised\\model");
        model.save(modelFile);
    }
}
