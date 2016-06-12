angular.module('cwh.spinner', []).directive('cwhSpinner', function() {
    return {
        restrict: 'AE', //Use as attribute and element
        template: 	'<span class="input-group-btn">' +
						'<a class="btn btn-primary" ng-click="goClick()">Go</a>' +
        			'</span>' +
        			'<input type="text" class="form-control" ng-keyup="setModel()" placeholder="placeholder">' +
        			'<span class="input-group-addon input-large btn btn-success" ng-click="increment()">+</span>' + 
        			'<span class="input-group-addon input-large btn btn-danger" ng-click="decrement()">-</span>',
        scope: { },
        require: 'ngModel',
        link: function(scope, iElement, iAttrs, ngModelController, ngAnimate) {//TODO: should be using formatters and parsers for this.
        	var increment = iAttrs.inc == null?1:parseFloat(iAttrs.inc);
        	var min = iAttrs.min == null?0:parseFloat(iAttrs.min);
        	var max = iAttrs.max == null?100:parseFloat(iAttrs.max);
        	iElement.find('input')[0].placeholder = iAttrs.placeholder == null?"":iAttrs.placeholder;
        	
            ngModelController.$render = function() {
                iElement.find('input')[0].value = ngModelController.$viewValue;
            };
            function incrementModel(inc) {
                ngModelController.$setViewValue(ngModelController.$viewValue + inc);
                ngModelController.$render();
            }
            scope.setModel = function() {
            	var input = parseFloat(iElement.find('input')[0].value);
            	if (input > max) {
            		iElement.find('input')[0].value = max;
            		input = max;
            	}
            	if (input < min) {
            		iElement.find('input')[0].value = min;
            		input = min;
            	}
            	ngModelController.$setViewValue(input);
            }
            scope.increment = function() {
            	incrementModel(increment);
            }
            scope.decrement = function() {
            	incrementModel(-increment);
            }
            scope.goClick = function() {
            	scope.$parent.$eval(iAttrs.goclick);
            }
        }
    }
});