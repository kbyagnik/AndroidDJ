function vmed = med(v,w)
disp('med');

hw = floor(w/2) ; % half window length
vext = [v(1)*ones(hw,1) ; v ; v(end)*ones(hw,1)];
vmed = medfilt1(vext,w);
vmed = vmed(hw+1:end-hw);
