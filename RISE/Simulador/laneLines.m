% Llegim una imatge de la que coneixem exactament el pitch 
% (carretera ideal, pitch = nominal). Donats 4 punts calculem
% l'equacio de les dues corbes. A partir dels 4 parametres de
% les dues equacions, x_0, a_left, a_right, i b més el
% cinque, y_0 = assimptota horitzontal, que ja es conegut a
% partir de f i phi, obtenim els 4 parametres theta, x_c, L i C,
% per comparar-los amb els reals (ground truth).
% Afecta la traslacio i reflexio del sistema de coordenades ?

clear all
close all
disp('laneLines');

nFrame = 400 % uint32(round(400*rand(1)))
ima = imread(['C:\Users\samsung\Desktop\RISE\1.jpg']);%Simulador\RoadIdealDetectionFromImages\' num2str(nFrame,'%06u') '
[nLin, nCol,z_edited] = size(ima) ;
ima = [uint8(255+zeros(nLin,nCol,3)) ; ima];
[nLin, nCol] = size(ima) ;


figure(1), imshow(ima), axis on, title(num2str(nFrame))
[x, y] = ginput(4) ; % dos punts a la lina de l'esquerra i despres dos a la de la dreta
hold on, plot(x, y,'ro'), hold off

E_u = 900 ;
E_v = E_u ;
phi = 2.0*pi/180.0 ; % nominal camera pitch
H = 1.5 ; % cameraHeight 
y0 = nLin/2 + E_u*tan(phi) ;

A = [ 1 (y(1)-y0)   0  1/(y(1)-y0) ; ...
      1 (y(2)-y0)   0  1/(y(2)-y0) ; ...
      1  0   (y(3)-y0) 1/(y(3)-y0) ; ...
      1  0   (y(4)-y0) 1/(y(4)-y0) ] ;
coef = inv(A)*x ;
x0 = coef(1) ;
aLeft = coef(2) ;
aRight = coef(3) ;
b = coef(4) ;

[xres,yres] = hiperbola([1:nCol],y0, [x0, aLeft, b]);
figure(1), hold on, plot(xres,yres,'r-'), hold off
[xres,yres] = hiperbola([1:nCol],y0, [x0, aRight, b]);
figure(1), hold on, plot(xres,yres,'r-'), hold off



x = x-nCol/2 ;
y = nLin/2-y ;
y0 = -E_u*tan(phi);
A = [ 1 (y(1)-y0)   0  1/(y(1)-y0) ; ...
      1 (y(2)-y0)   0  1/(y(2)-y0) ; ...
      1  0   (y(3)-y0) 1/(y(3)-y0) ; ...
      1  0   (y(4)-y0) 1/(y(4)-y0) ] ;
coef = inv(A)*x ;
x0 = coef(1) ;
aLeft = coef(2) ;
aRight = coef(3) ;
b = coef(4) ;

thetaImage = cos(phi)*x0/E_u ;
x_cImage = E_v*H*aLeft/(E_u*cos(phi)) ;
CImage = 2*2*cos(phi)*cos(phi)*cos(phi)*b/(E_v*E_u*H) ; %% perque cal 2* !??!
LImage = x_cImage - aRight*(E_v*H/(E_u*cos(phi))) ;

computedImage = struct('theta', thetaImage, ...
                       'x_c',   x_cImage, ...
                       'C',     CImage, ...
                       'L',     LImage  );

load 'C:\Users\samsung\Desktop\RISE\Simulador\RoadIdealDetectionPerfect\results.mat'
disp('Ground truth : theta (deg.) x_c C L')
[groundTruth.theta(nFrame)*180/pi   groundTruth.x_c(nFrame)   groundTruth.C(nFrame)   groundTruth.L(nFrame)]
disp('Computed from image')
[computedImage.theta*180/pi    computedImage.x_c    computedImage.C    computedImage.L]




% % equacio d'una hiperbola amb assimptota a y0 donats 3 punts :
% ima = imread('000150.jpg');
% figure(10), imshow(ima), axis on;
% [nLin, nCol] = size(ima) ;
% % [x0, y0] = ginput(1) ; % assimptota y = y0
% y0 = nLin/2+E_u*tan(phi)
% [x, y] = ginput(3) ; % 3 punts
% hold on, plot([1 nCol], [y0 y0],'r-'), plot(x, y,'ro'), hold off
% 
% A = [ones(3,1) y-y0 1./(y-y0)] ;
% coef = inv(A)*x ;
% [xres,yres] = hiperbola([1:nCol],y0,coef);
% figure(1), hold on, plot(xres,yres,'r-'), hold off
% coef
% % modificar coef(2), calcular uns nous ymins, yplus i dibuixar a sobre : SI
% % que coincideix amb la linia de carril de l'altre canto nomes canviant
% % coef(2) !
