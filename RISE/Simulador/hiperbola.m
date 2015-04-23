% Samples y in hyperbola (x-x0) = a*(y-y0) + b/(y-y0)
% for values in x
%
function [xres,yres] = hiperbola(x, y0, coef) % coef = [x0 a b]

disp('hiperbola');

x = reshape(x,length(x),1);
a = coef(2) ;
b = -2*coef(2)*y0-x+coef(1) ;
c = coef(2)*y0*y0+coef(3)+x*y0-coef(1)*y0 ;
yplus = (-b + sqrt(b.*b - 4*a*c))/(2*a) ;
yminus = (-b - sqrt(b.*b - 4*a*c))/(2*a) ;
% iplus = find((yplus>y0) & isreal(yplus)) ;
% iminus = find((yminus>y0) & isreal(yminus));
iplus = find(yplus>y0) ;
iminus = find(yminus>y0) ;
yres = [yminus(iminus) ; yplus(iplus)] ;
xres = [x(iminus) ; x(iplus)] ;