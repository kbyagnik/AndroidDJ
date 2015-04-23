function p = pitch(roadLength, ...
                   highFrequencyMaximumAmplitude, cutOffFrequency, ...
                   minimumSeparation, maximumSeparation, lowFrequencyMaximumAmplitude, lowFrequencyMinimumAmplitude, triangleWidth)
% roadLength=2000;
% % Low frequency parameters
% highFrequencyMaximumAmplitude = 0.2 ; % in degrees. Minimum amplitude is zero
% cutOffFrequency = 60;
% % High frequency parameters
% minimumSeparation = 200 ; 
% maximumSeparation = 400 ;
% lowFrequencyMaximumAmplitude  = 1.0 ; % in degrees
% lowFrequencyMinimumAmplitude  = 0.5 ;
% triangleWidth = 101 ; % must be odd and less than minimumSeparation

% High frequency component : low pass filter of uniform noise


disp('pitch');

hf = rand(roadLength,1) ;
HF = fft(hf);
HF(2+cutOffFrequency:end-cutOffFrequency) = 0 ; % low-pass filter
hfLowPassFiltered = ifft(HF);
% rescale to +/- highFrequencyMaximumAmplitude
hfLowPassFiltered = ( (hfLowPassFiltered - min(hfLowPassFiltered))/ ...
                      (max(hfLowPassFiltered)-min(hfLowPassFiltered)) - 0.5) ...
                      *2*highFrequencyMaximumAmplitude;

% Low frequency ; we place a copy of a triangle of random height and width w
% at a distance equal to uniform random noise.
nImpulses = roadLength/minimumSeparation ; % maximum possible number of impulses
tImpulses = round(cumsum(minimumSeparation + rand(nImpulses,1)*maximumSeparation));
ii = max(find(tImpulses<roadLength))-1;    % last safe impulse to place a triangle on it
tImpulses = tImpulses(1:ii);
lf = zeros(roadLength,1);
% set a random height to each impulse
lf(tImpulses) = (rand(ii,1)-0.5)*2*(lowFrequencyMaximumAmplitude-lowFrequencyMinimumAmplitude);
lf(tImpulses) = lf(tImpulses) + sign(lf(tImpulses)).*lowFrequencyMinimumAmplitude;
triangle = triang(triangleWidth);
lf = conv(lf,triangle);
lf = lf(1:roadLength);

p = lf + hfLowPassFiltered ;
maxAmp = max(lowFrequencyMaximumAmplitude,highFrequencyMaximumAmplitude);
p = min(p,maxAmp);
p = max(p,-maxAmp);

p = p*pi/180.0;




% sigmaPitch = 20 ;                   % controls how sudden (or smooth) is the pitch change.
%                                     % in meters, to build the smoothing gaussian filter
%                                     % and low-pas filter the uniform pitch noise    
% pitchNoise = 2*(rand(roadLength,1)-0.5); % uniform noise [-1,1]
% windowSizePitch = 8*sigmaPitch ;
% t = double(-windowSizePitch/2:windowSizePitch/2);
% % reduce noise frequency
% filter = (1/(sqrt(2*pi)*sigmaPitch))*exp(-(t.*t)/(2*sigmaPitch*sigmaPitch));
% pitchNoise = smoothVector(pitchNoise, filter);
% % now re-scale noise to interval [-maxPitchVariation, +maxPitchVariation]
% minPitchNoise = min(pitchNoise) ;
% maxPitchNoise = max(pitchNoise) ;
% cameraPitch = nominalCameraPitch + ...
%               maxPitchVariation*2*((pitchNoise-minPitchNoise)/(maxPitchNoise-minPitchNoise)-0.5) ;
