% FALTA ACABAR
function res = myatan2(y,x)
disp('myatan2');

%ATAN2  Four quadrant inverse tangent.
%   ATAN2(Y,X) is the four quadrant arctangent of the real parts of the
%   elements of X and Y.  -pi <= ATAN2(Y,X) <= pi.
%
%   See also ATAN.

%   Copyright 1984-2003 The MathWorks, Inc.
%   $Revision: 5.8.4.2 $  $Date: 2004/04/16 22:05:51 $
%   Built-in function.
epsilon = 0.005;
if ((abs(x)<epsilon) & (abs(y)
else
    res = builtin('atan2', y,x);
end
