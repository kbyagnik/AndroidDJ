function err = rmse(a,b)
disp('rmse');

dif = reshape(a,1,length(a)) - reshape(b,1,length(b)) ;
err = sqrt(mean(dif.*dif)) ;