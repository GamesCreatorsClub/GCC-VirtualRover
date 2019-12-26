import logging

logger = logging.getLogger(__name__)

class PIDController(object):
    def __init__(self, proportional_constant=0, integral_constant=0, derivative_constant=0, windup_limit=None):
        self.proportional_constant = proportional_constant
        self.integral_constant = integral_constant
        self.derivative_constant = derivative_constant
        self.windup_limit = windup_limit
        # Running sums
        self.integral_sum = 0
        self.last_error = None

    def reset(self):
        self.integral_sum = 0

    def handle_proportional(self, error):
        return self.proportional_constant * error

    def handle_integral(self, error):
        """Integral will change if
            * There is no windup limit
            * We are below the windup limit
            * or the sign of the error would reduce the sum"""
        if self.windup_limit is None or \
                (abs(self.integral_sum) < self.windup_limit) or \
                ((error > 0) != (self.integral_sum > 0)):
            self.integral_sum += error
        return self.integral_constant * self.integral_sum

    def handle_derivative(self, error):
        """ """
        if self.last_error is not None:
            diff = error - self.last_error
        else:
            diff = 0
        self.last_error = error
        return self.derivative_constant * diff

    def get_value(self, error):
        p = self.handle_proportional(error)
        i = self.handle_integral(error)
        d = self.handle_derivative(error)
        value = p + i + d
        logger.info(f"error: {error}, p: {p}, i: {i}, d: {d} = value: {value}")
        return value
