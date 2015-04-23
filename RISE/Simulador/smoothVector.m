% 1D convolution by first repeating initial and final value as 
% many times as half the filter size and then taking the central part.
% Filter length must be odd.
%
function res = smoothVector(v,filter)
disp('smoothvector');

s = length(filter);
tmp = conv( [v(1)*ones(s,1) ; v ; v(end)*ones(s,1) ], filter);
res = tmp( s+ (s+1)/2 : end-s-(s-1)/2);
