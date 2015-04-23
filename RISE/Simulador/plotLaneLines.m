function plotLaneLines(u_l, v_l, u_r, v_r, E_v, phi, nCol, nLin, hfFrames)


disp('plotlanes');

v_lprime = v_l/E_v + tan(phi);
v_rprime = v_r/E_v + tan(phi);    
M = [ ones(2,1) zeros(2,1)  v_lprime'  1./v_lprime' ; ...
      ones(2,1) v_rprime'   v_rprime'  1./v_rprime'   ...
    ] ; 
b = [u_l' ; u_r'] ;
a = inv(M)*b ;

u0     = a(1) ;
aLeft  = a(3) ;
aRight = a(2)+a(3) ;
b      = a(4) ;

v0 = nLin/2+E_v*tan(phi) ;

[x,y] = hiperbola([-nCol/2:nCol/2],v0, [u0, aLeft, b]);
y = (y-tan(phi))*E_v ;
ii = find((y>=-nLin/2) & (y<=nLin/2)) ;
figure(hfFrames), hold on, plot(x(ii),y(ii),'ko'), hold off
[x,y] = hiperbola([-nCol/2:nCol/2],v0, [u0, aRight, b]);
y = (y-tan(phi))*E_v ;
ii = find((y>=-nLin/2) & (y<=nLin/2)) ;
figure(hfFrames), hold on, plot(x(ii),y(ii),'ko'), hold off

    
