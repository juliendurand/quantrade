package blackbox.predictor;

public abstract class APredictor {
	
	protected double[] _parameters;
	
	public abstract double estimate(double[] input);
	
	public void setParameters(double[] parameters) {
		_parameters = parameters;
	}

}
