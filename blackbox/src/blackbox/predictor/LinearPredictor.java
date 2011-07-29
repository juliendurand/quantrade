package blackbox.predictor;

public class LinearPredictor extends APredictor{

	@Override
	public double estimate(double[] input) {
		double result = 0d;
		for(int i=0;i<input.length;i++){
			result += input[i]*_parameters[i];
		}
		return result;
	}
}
