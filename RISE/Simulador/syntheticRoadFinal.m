%----------------
% Initializations
%----------------
% Linux 
cd 'C:\Users\samsung\Desktop\RISE\Simulador\'

disp('ROADFINAL');


% Windows
% cd 'd:\Documents and Settings\joans\Mis documentos\Article linies de carril\Simulador'
disp('---------------------------------------------------------------------')
warning off
clear all
close all
rand('state',3) % random numbers fixed in order to do repetitive tests  

flatRoad = 0 ;
straightRoad = 0;
pitchVariation = 1 ; % controls whether camera pitch changes or not.
linearlyInterpolateCurvature = 1 ;  % linearly interpolate the piecewise constant curvature

viewMovie = 1 ;
plotTrueLaneLines = 0;
saveMovie = 0 ; % to save the movie, it is necessary that viewMovie = 1
saveResult = 0 ;
nameResult = 'Non-ideal camera'; % 'Non-ideal road', 'Non-ideal camera', 


%----------------------------------------------
% Selection of test case for results generation
%----------------------------------------------
% Terminology :
%   ideal road = piecewise constant curvature, constant height, constant
%                camera pitch
%   realistic road = constant or linearly variying curvature, variable
%                height and camera pitch
%   perfect detection = two points on each lane line are perfectly detected
%                using known road geometrical shape and camera parameters
%   detection on images = points are extracted from images of the synthetic
%                road 
%
% For 'perfect detection', computation of parameters (curvature, lane width, 
% lateral displacement and yaw angle) are performed in Matlab. For 'detection 
% on images', by the C++ program but in both cases the computations are the same.
% However, in the later case, frames are saved on disk to be later analyzed by 
% the C++ program with crease detection+Ransac etc.

            
%--------------------
% Road geometry setup
%--------------------
% The road is composed of two lanes, two border lane markings and a central 
% line which separes the two lanes. Lane width is constant.
% Curvature is constant or linearly varying. Height varies also but
% independently of curvature so that curvature and height 
% changes do not coincide in space giving rise to "unlikely" or worse case 
% disagreement  between the road model for lane markings detection and
% ground truth.
% Lane markings lines and lanes have constant width. All distance units are
% in meters.
offsetBorder        = 100 ;                             % meters added to road length in order to avoid road end effects
roadLength          = 5000 + offsetBorder;              % meters
minSegmentLengthC   = 300;                              % to avoid sudden height and curvature changes, limit minimum length
maxSegmentLengthC   = 600;                              % to promote a varied road, we limit the maximum length
minCurvatureRadius  = 50.0;                             % determines maximum curvature magnitude
maxCurvature        = 1.0/minCurvatureRadius ;          % maximum curvature magnitude
maxCurvatureChange  = maxCurvature*0.75 ;               % maximum change of curvature in adjacent segments

windowSizeCurvature = int32(minSegmentLengthC/2) ;      % According to road model, curvature is constant or linearly varying
minSegmentLengthH   = 300;                              % to avoid sudden height and curvature changes, limit minimum length
maxSegmentLengthH   = 600;                              % to promote a varied road, we limit the maximum length
maxSlope            = 0.07*(1-flatRoad) ;               % 0.1 is a 10% slope = atan(0.1)*180/pi = 5.7 degrees, 0.0 produces a flat road
sigmaHeight         = 5.0 ;
windowSizeHeight    = int32(minSegmentLengthH/4) ;      % To smooth height
groundWidth         = 5.0;
vergeWidth          = 1.0;
laneWidth           = 3.5;                              % does not include lane lines
interLaneLineWidth  = 0.1;                              % Other possible values :
interLaneLineLength = 4;                                %  2  4  5
interLaneLineGap    = 7;                                %  6  9 12
borderLaneLineWidth = 0.2;                              %  0.2 0.15 0.1
borderLaneLineLength = 20;                              %  20
borderLaneLineGap   = 4;                                %  4

%----------------------
% Road photometry setup
%----------------------
groundGreyLevel         = 0.6 ;
vergeGreyLevel          = 0.3 ;
lanelineGreyLevel       = 0.9 ;
laneGreyLevel           = 0.2 ;
% greyLevelNoiseAmplitude = 0.0 ; % ja no es fa servir
% minimum and maximum length of segments of constant grey level contrast and
% brightness
minLength = 40 ;
maxLength = 100 ;
windowSizeContrastBrighness = 3 ;
minContrast = 0.8 ;
maxContrast = 1.0 ;
minBrightness = 0.0 ;
maxBrightness = 0.2 ;


%------------------
% Camera properties
%------------------
% Set to match horizon line height and lane width similar to that found in real videos
f = 1200 ;                          % focal distance in pixels
cameraHeight = 1.6 ;                % above the road
nominalCameraPitch = 1.6*pi/180.0 ; % actual camera pitch in a zero slope road
                                    % This combination of f, H and \phi yield a 
                                    % road horizon line at 40 pixels below the
                                    % central row.
sigmaCameraNoise = 0.0 ;
% Pitch variation parameters. Amplitudes are in degrees, separations and width in meters :                                    
    % Low frequency parameters
    highFrequencyMaximumAmplitude = 0.3 ; % Minimum amplitude is zero
    cutOffFrequency = 0.03*roadLength;
    % High frequency parameters
    minimumSeparation = 200 ; 
    maximumSeparation = 400 ;
    
    lowFrequencyMaximumAmplitude  = 1.0 ; 
    lowFrequencyMinimumAmplitude  = 0.5 ;
    triangleWidth = 101 ; % must be odd and less than minimumSeparation
                                    
                                 
%--------------------------------
% Vehicle (=> camera) trajectory
%--------------------------------
minSegmentLengthLD = 50 ;
maxSegmentLengthLD = 100 ;
sigmaLD = minSegmentLengthLD/2 ;
windowSizeLD = 8*sigmaLD ;
maximumLateralDisplacement =  0.8*(laneWidth/2) ;

%-----------------
% Movie properties
%-----------------
nCol = 640 ;
nLin = 480 ;
patchesAhead = 3000 ; % To display from present patch to present plus this number of patches

%--------------------
% Lane line detection
%--------------------
detectionSigma = 0 ;    % in pixels, sigma of gaussian noise added to the coordinates of perfect detection
                        % set to zero for a perfect detection
maxLin = -125 ; % when computing from image points, take only into account those within the image
                % and below this row. This limits the (bad) influence of changing
                % height and curvature in the results.


%--------------------------------------------------
% Road construction : Segments and Curvature
%--------------------------------------------------
% maximum possible number of segments of constant curvature
nMaxSegmentsC = roadLength/minSegmentLengthC ; 
% curvature of each segment with sign
segmentCurvature = maxCurvature*(2.0*rand(1,nMaxSegmentsC)-1.0);
if (straightRoad)
    segmentCurvature = segmentCurvature*0;
end
% Limit the change in curvature between successive segments by
% inserting new segments where curvature change exceeds the threshold. This
% implies a shift and discard of last segments
newSegmentCurvature = [];
for i=2:nMaxSegmentsC
    newSegmentCurvature = [newSegmentCurvature segmentCurvature(i-1)];
    curvatureChange = abs(segmentCurvature(i) - segmentCurvature(i-1));
    if (curvatureChange>maxCurvatureChange)
        % insert new segments 
        numSegmentsToInsert = floor(curvatureChange/maxCurvatureChange);
        c = linspace(segmentCurvature(i-1),segmentCurvature(i),...
                                        numSegmentsToInsert+2) ;
        newSegmentCurvature = [newSegmentCurvature c(2:end-1)];
    end
end
segmentCurvature = newSegmentCurvature(1:nMaxSegmentsC) ;
% set the length of each segment and then keep the first ones which
% are enough to make the given road length
segmentLengthC = round(minSegmentLengthC + ...
                       rand(1,nMaxSegmentsC)*(maxSegmentLengthC - minSegmentLengthC));
csH = cumsum(segmentLengthC);
nSegmentsC = length(find(cumsum(segmentLengthC)<roadLength)) ;
% extend length of last segment to complete the road
segmentLengthC(nSegmentsC) = roadLength - sum(segmentLengthC(1:nSegmentsC-1)); 
segmentLengthC = segmentLengthC(1:nSegmentsC);
segmentCurvature = segmentCurvature(1:nSegmentsC);
% to avoid road self crossing (loops) within one same segment, we may 
% need to reduce its curvature (in magnitude)
segmentCurvature = sign(segmentCurvature).* ...
                   min(abs(segmentCurvature),(ones(1,nSegmentsC)*pi)./segmentLengthC);

% from curvature per segment to curvature per meter so that we can 
% smooth it later
roadCurvature = []; 
for i=1:nSegmentsC
    roadCurvature  = [roadCurvature ; segmentCurvature(i)*ones(segmentLengthC(i),1)];
end
if (linearlyInterpolateCurvature)
    % Road curvature smooting. Curvature must be linearly smoothed to follow the clothoid road model.
    filter = (1.0/double(windowSizeCurvature))*ones(windowSizeCurvature,1);
    roadCurvature = smoothVector(roadCurvature, filter);
end

%-------------------------------------
% Road construction : Height
%-------------------------------------
nMaxSegmentsH = roadLength/minSegmentLengthH ; % maximum possible number of segments of constant slope
segmentLengthH = round(minSegmentLengthH + rand(1,nMaxSegmentsH)*(maxSegmentLengthH - minSegmentLengthH));
csH = cumsum(segmentLengthH);
nSegmentsH = length(find(cumsum(segmentLengthH)<roadLength)) ;
segmentLengthH(nSegmentsH) = roadLength - sum(segmentLengthH(1:nSegmentsH-1)); % extend length of last segment to complete the road
segmentLengthH = segmentLengthH(1:nSegmentsH);
segmentSlope = maxSlope*2*(rand(nSegmentsH,1)-0.5);
% Avoid change from/to negative to/from positive slope : in
% between force a zero slope segment. This way we avoid unrealistic pits
% and peaks in height
segmentSlope(find([0 ; sign(segmentSlope(2:end))-sign(segmentSlope(1:end-1))]))=0;
segmentHeight(1) = 0 ; % the road is flat at first
for i=2:nSegmentsH
    segmentHeight(i) = segmentHeight(i-1) + segmentLengthH(i)*segmentSlope(i) ;
end
roadHeight = [];
roadSlope = [];
roadHeight = [segmentHeight(1)*ones(segmentLengthH(1),1)];
% Height linear interpolation
for i=2:nSegmentsH
    roadHeight = [roadHeight ; ...
                  segmentHeight(i-1) + (segmentHeight(i)-segmentHeight(i-1))*[1:segmentLengthH(i)]'/double(segmentLengthH(i))];
end
% Gaussian smoothing to avoid height derivative discontinuities, which are
% not natural and unrealistic
t = double(-windowSizeHeight/2:windowSizeHeight/2);
filter = (1/(sqrt(2*pi)*sigmaHeight))*exp(-(t.*t)/(2*sigmaHeight*sigmaHeight));
roadHeight = smoothVector(roadHeight, filter);
roadSlope = (roadHeight(3:end) - roadHeight(1:end-2))/2;
roadSlope = [roadSlope(1) ; roadSlope];
roadSlope(end+1) = roadSlope(end);


%--------------------------------------------------------------------
% Road planar shape (computation of the vertices of all road patches) 
%--------------------------------------------------------------------
roadMedialAxis(1,:) = [0 0 0];                    % initial position
vn(1,:) = [0 1 0];                                %    "    road orientation coincident with Y axis
alpha(1) = 0.0;
for i=2:roadLength                                % calculation of the next postions and orientations
    alpha(i) = asin(0.5*roadCurvature(i));        % angle between road tangent lines at two 
                                                  % points 1 meter appart since arc length is
                                                  % approximately = 1 meter
    vn(i,:) = [ -sin(sum(alpha)) cos(sum(alpha)) 0 ]; 
    roadMedialAxis(i,:) = [roadMedialAxis(i-1,1)+vn(i,1) ...
                           roadMedialAxis(i-1,2)+vn(i,2) ...
                           roadHeight(i) ]; 
end

% Per fer que en canvi de colors de la carretera no canvii tambe les
% figures ja generades, donat que la trajectoria es genera a partir de
% numeros aleatoris que serien diferents

% S = rand('state');
% save state.mat S
load state.mat % variable S
rand('state',S);

%-------------------------------------
% Computation of the camera trajectory
%-------------------------------------
% maximum possible number of segments of constant lateral displacement
nSegmentsLD = roadLength/minSegmentLengthLD ; 
segmentLengthLD = round(minSegmentLengthLD + ...
                  rand(1,nSegmentsLD)*(maxSegmentLengthLD - minSegmentLengthLD));
csH = cumsum(segmentLengthLD);
nSegmentsLD = length(find(cumsum(segmentLengthLD)<roadLength)) ;
% extend length of last segment to complete the road
segmentLengthLD(nSegmentsLD) = roadLength - sum(segmentLengthLD(1:nSegmentsLD-1)); 
segmentLengthLD = segmentLengthLD(1:nSegmentsLD);
segmentLD(1) = (laneWidth/2) ; % begin at the center of the right lane
for i=2:nSegmentsLD
    % keep always in the lane to the right of the road center (two lanes=>right lane)
    segmentLD(i) = (laneWidth/2) + maximumLateralDisplacement*2*(rand(1)-0.5) ; 
end
ld = [] ;
for i=1:nSegmentsLD
    ld  = [ld ; segmentLD(i)*ones(segmentLengthLD(i),1)];
end
% heavy smoothing
t = double(-windowSizeLD/2:windowSizeLD/2);
filter = (1/(sqrt(2*pi)*sigmaLD))*exp(-(t.*t)/(2*sigmaLD*sigmaLD));
ld = smoothVector(ld, filter);

xCameraTrajectory = roadMedialAxis(:,1) + ld.*vn(:,2);
yCameraTrajectory = roadMedialAxis(:,2) + ld.*(-vn(:,1));
zCameraTrajectory = roadHeight + cameraHeight;
cameraTrajectory = [xCameraTrajectory, yCameraTrajectory, zCameraTrajectory];


%-------------
% Camera pitch
%-------------
if (pitchVariation)
    pitchNoise = pitch(roadLength, highFrequencyMaximumAmplitude, cutOffFrequency, ...
                       minimumSeparation, maximumSeparation, lowFrequencyMaximumAmplitude, ...
                       lowFrequencyMinimumAmplitude, triangleWidth);
    cameraPitch = nominalCameraPitch + pitchNoise ;
else
    cameraPitch = nominalCameraPitch*ones(roadLength,1);
end

%---------------------
% Paint camera patches
%---------------------
% A lateral slice of a two lanes road (orthogonal to the road media axis) is made
% of several types of terrain : ground, left verge, left boder lane line,
% left lane, central lane line, right lane, right lane line border, right 
% verge and ground again.
% The X coordinates of the vertices of terrain patches along the whole road, with 
% respect the reference frame centered at the medial road axis (X pointing 
% to the right, Y forward, Z upward) are computed and stored in vertices :
v = [ - (groundWidth + vergeWidth + borderLaneLineWidth + laneWidth + interLaneLineWidth/2),  ...
      - (              vergeWidth + borderLaneLineWidth + laneWidth + interLaneLineWidth/2),  ...
      - (                           borderLaneLineWidth + laneWidth + interLaneLineWidth/2),  ...
      - (                                                 laneWidth + interLaneLineWidth/2),  ...
      -                                                               interLaneLineWidth/2 ,  ...
         interLaneLineWidth/2                                                        ,  ...
         interLaneLineWidth/2 + laneWidth                                            ,  ...
         interLaneLineWidth/2 + laneWidth + borderLaneLineWidth                            ,  ...
         interLaneLineWidth/2 + laneWidth + borderLaneLineWidth + vergeWidth               ,  ...
         interLaneLineWidth/2 + laneWidth + borderLaneLineWidth + vergeWidth + groundWidth  ]'*[1 0 0];
lc = [ -(borderLaneLineWidth/2 + laneWidth + interLaneLineWidth/2),  ... % lane line centers in the first segment
        0.0,                                    ...
        (interLaneLineWidth/2 + laneWidth + borderLaneLineWidth/2) ]'*[1 0 0];
nVerticesAcross = size(v,1); % number of vertices in a line across the road    
greyLevels = [ groundGreyLevel vergeGreyLevel lanelineGreyLevel laneGreyLevel ...
               lanelineGreyLevel ...
               laneGreyLevel lanelineGreyLevel vergeGreyLevel groundGreyLevel ] ;              
nGreyLevels = length(greyLevels);           
vertices = v ;
faces    = [];
colors   = []; % gray level of each patch
beta     = 0.0 ;
laneLineCenters = lc ;

contrast = [];
while (length(contrast)<roadLength)
    contrast = [contrast (minContrast + (maxContrast-minContrast)*rand(1))*ones(1,minLength + maxLength*rand(1))] ;
end
contrast = contrast(1:roadLength);
filter = (1.0/double(windowSizeContrastBrighness))*ones(windowSizeContrastBrighness,1);
contrast = smoothVector(contrast', filter);
brightness = [];
while (length(brightness)<roadLength)
    brightness = [brightness (minBrightness + (maxBrightness-minBrightness)*rand(1))*ones(1,minLength + maxLength*rand(1))] ;
end
brightness = brightness(1:roadLength);
brightness = smoothVector(brightness', filter);


for i=1:roadLength                                     
    beta = beta + asin(0.5*roadCurvature(i)); % road tangent line angle
    R =  [ cos(beta) sin(beta) 0 ; ...
          -sin(beta) cos(beta) 0 ; ...
           0         0         1 ] ;
    laneLineCenters = [laneLineCenters ; lc*R + ones(3,1)*roadMedialAxis(i,:) ] ;     

    vv=v*R ; % rotation of initial vertices
    vv = vv + ones(nVerticesAcross,1)*roadMedialAxis(i,:) ; % translation
    vertices = [ vertices ; vv];
    faces = [ faces                  ; ...
              [ 1  2 12 11] + (i-1)*nVerticesAcross ; ...
              [ 2  3 13 12] + (i-1)*nVerticesAcross ; ...
              [ 3  4 14 13] + (i-1)*nVerticesAcross ; ...
              [ 4  5 15 14] + (i-1)*nVerticesAcross ; ...
              [ 5  6 16 15] + (i-1)*nVerticesAcross ; ...
              [ 6  7 17 16] + (i-1)*nVerticesAcross ; ...
              [ 7  8 18 17] + (i-1)*nVerticesAcross ; ...
              [ 8  9 19 18] + (i-1)*nVerticesAcross ; ...
              [ 9 10 20 19] + (i-1)*nVerticesAcross ];
          
    % Ja no canviem la intensitat dels patches a cada metre :
    % greyLevelsPatch = greyLevels + greyLevelNoiseAmplitude.*(rand(1,nVerticesAcross-1)-0.5) ;
    %
    greyLevelsPatch = greyLevels*contrast(i) + brightness(i) ; 
    greyLevelsPatch = max(zeros(1,nGreyLevels),greyLevelsPatch); % keep grey levels within [0,1]
    greyLevelsPatch = min(ones(1,nGreyLevels),greyLevelsPatch);
    % Check for gap in dashed interlane line (0:gap, 1:line)
    m = mod(i,interLaneLineLength+interLaneLineGap);
    centralLineGap = (interLaneLineGap>0) & ((m==0) | (m>interLaneLineLength));
    % Check for gap in dasehd border line (0:gap, 1:line)
    m = mod(i,borderLaneLineLength+borderLaneLineGap);
    borderLineGap = (borderLaneLineGap>0) & ((m==0) | (m>borderLaneLineLength)); 
    if (borderLineGap) % Set the gap to the lane grey level
        greyLevelsPatch(3) = greyLevelsPatch(4);
        greyLevelsPatch(7) = greyLevelsPatch(6);
    end
    if (centralLineGap) % Mean of left and right lanes grey level
        greyLevelsPatch(5) = (greyLevelsPatch(4)+greyLevelsPatch(6))/2;
    end
    colors = [ colors ; (ones(3,1)*greyLevelsPatch)' ];
end


%---------------------------------------------------
% Projection of 3D road patches into the image plane
% and movie (frames) generation
%---------------------------------------------------
K = [f 0 0 ; 
     0 f 0 ;
     0 0 1 ];     % projection matrix
nFaces = size(faces,1);
nVertices = size(vertices,1);
nLaneLineCenters = size(laneLineCenters,1); 
% = (roadLength+1)*3, 3 because of 3 lane lines (two lanes) in the road
n = 0 ; % Adjust so that a visualization error of matlab patches
        % does not show any more for a given sequence.
        % This error happens when patches near the camera become
        % visible due to large steering angles (yaw angle) that is,
        % when the vehicle (camera) trajectory has a tangent direction
        % quite different from the road medial axis direction.
        % The effect is to remove from display some patches of the
        % lower part of the image.
if (viewMovie)        
        figure
        hfFrames = gcf ;
end       

%---------------------------------------------------
% Computation of parameters L, theta, x_c and C from 
% the synthetic road
%---------------------------------------------------
computed = struct('L',     [], ...
                  'theta', [], ...
                  'x_c',   [], ...
                  'C',     [], ...
                  'rcond', []  ...
                 ) ;

for i=1:roadLength - offsetBorder
    cameraPosition = cameraTrajectory(i,:) ; 
    % realPhi is the pitch angle with respect an absolute world coord.
    % system, while cameraPitch is with respect the road plane at each
    % frame
    realPhi(i) = cameraPitch(i) + atan(roadSlope(i)) ; 
    % The projection of the viewing direction vector on the road is 
    % tangent to the vehicle trajectory
    if (i==1)
        dx(1) = 0.0 ;
        dy(1) = 1.0 ;
        d = 1.0 ;
    else
        dx(i) = xCameraTrajectory(i)-xCameraTrajectory(i-1) ;
        dy(i) = yCameraTrajectory(i)-yCameraTrajectory(i-1);
        d = sqrt(dx(i)*dx(i)+dy(i)*dy(i)) ;
    end
    cameraViewingDirection(i,:) = [dx(i)/d, dy(i)/d, sin(realPhi(i))] ;
    cameraViewingDirection(i,:) = cameraViewingDirection(i,:)./norm(cameraViewingDirection(i,:)) ;   
    cameraYAxis = cameraViewingDirection(i,:) ; % camera Y axis is the viewing direction    
    cameraXAxis=cross(cameraViewingDirection(i,:),[0,0,1]);
    cameraXAxis=cameraXAxis/norm(cameraXAxis);
    cameraZAxis=cross(cameraXAxis,cameraViewingDirection(i,:));
    % Scene (patches vertices) perspective projecion into the image plane
    R = [ cameraXAxis ; 
          cameraZAxis ; 
          cameraYAxis] ;
    t = -R*cameraPosition';
    P = K * [R t] ;
    verticesFrame = P*[vertices' ; ones(1,nVertices)];
    verticesFrame = verticesFrame./(ones(3,1)*verticesFrame(3,:));
    laneLineCentersFrame = P*[laneLineCenters' ; ones(1,nLaneLineCenters)];
    laneLineCentersFrame = laneLineCentersFrame./(ones(3,1)*laneLineCentersFrame(3,:));
    
    numCenters = 200 ;
    nLaneLineCenters = size(laneLineCentersFrame,2);
    firstCenter = 3*i ;
    lastCenter = min(nLaneLineCenters,firstCenter+3*numCenters) ;
    
    leftMostLineCentersFrame  = filterCenters(laneLineCentersFrame(1:2,firstCenter+1:3:lastCenter),nLin,nCol,maxLin);
    middleLineCentersFrame    = filterCenters(laneLineCentersFrame(1:2,firstCenter+2:3:lastCenter),nLin,nCol,maxLin);
    rightMostLineCentersFrame = filterCenters(laneLineCentersFrame(1:2,firstCenter  :3:lastCenter),nLin,nCol,maxLin);
%     rowsLeftLine = [-75 -175];
%     [u_l,v_l] = laneLinePoints(middleLineCentersFrame, rowsLeftLine);
%     rowsRightLine = [-125 -225]; 
%     [u_r,v_r] = laneLinePoints(rightMostLineCentersFrame, rowsRightLine);
    u_l = middleLineCentersFrame(1,:)';
    v_l = middleLineCentersFrame(2,:)';
    u_r = rightMostLineCentersFrame(1,:)';
    v_r = rightMostLineCentersFrame(2,:)';
    
    % Computation : solving the linear system of equations.
    % To achieve a better conditioning and perhaps less error,
    % weight columns so that all matrix entries are near 1.0
    % normalizationFactors = [1 10 10 0.1]';
    normalizationFactors = [1 1 1 1]'; % no normalization

    % Translation of known parameters known from camera
    % to the new terminology of the paper's equations.
    phi = nominalCameraPitch ; 
    H   = cameraHeight ;
    E_u = f ;
    E_v = f ;
    
    % Add gaussian detection noise
    if (detectionSigma > 0)
        u_l = u_l + detectionSigma*randn(size(u_l));
        u_r = u_r + detectionSigma*randn(size(u_r));
        v_l = v_l + detectionSigma*randn(size(v_l));
        v_r = v_r + detectionSigma*randn(size(v_r));
    end
          
    % Solve for a = [a_1 a_2 a_3 a_4]' in Ma=b    
    v_lprime = v_l/E_v + tan(phi);
    v_rprime = v_r/E_v + tan(phi);    
    b = [u_l ; u_r] ;
    nCentersLeft = length(u_l);
    nCentersRight = length(u_r);
    M = [ ones(nCentersLeft,1)   zeros(nCentersLeft,1)   v_lprime  1./v_lprime ;  ...
          ones(nCentersRight,1)  -v_rprime               v_rprime  1./v_rprime    ...
         ] * diag(normalizationFactors); 
    % a = inv(M)*b ;
    a = M \ b;
    a = a.*normalizationFactors;

    % Compute parameters from a_i's
    computed.theta(i) = (cos(phi)/E_u)*a(1) ;
    computed.L(i)     = (H/(E_u*cos(phi)))*a(2) ;
    computed.x_c(i)   = (H/(E_u*cos(phi)))*a(3) ;
    computed.C(i)     = (4*((cos(phi))^3)/(E_u*H))*a(4) ;
    if (size(M,1)==size(M,2))
        computed.rcond(i) = rcond(M);
    else
        computed.rcond(i) = 0;
    end
        

    if (viewMovie)
        % Rendering of present to 'patchesAhead' patches, in reverse order
        lastFace = min(nFaces,patchesAhead+(i+n)*(nVerticesAcross-1));
        firstFace = (2*nVerticesAcross-1)+(i+n)*(nVerticesAcross-1);

        figure(hfFrames)
        patch('Faces',           faces(lastFace:-1:firstFace,:)  ,...
              'Vertices',        verticesFrame([1 2],:)'         ,...
              'FaceVertexCData', colors(lastFace:-1:firstFace,:) ,...
              'FaceColor','flat','Linestyle','none'); 
        text(0,220,num2str(i))
        axis([-nCol/2 nCol/2 -nLin/2 nLin/2])
        a = gca;
        set(a,'PlotBoxAspectRatio', [4 3 1]);
        set(hfFrames,'PaperPosition', [0.634517 6.34517 20.3046 15.2284], ...
                     'PaperSize',     [20.984 29.6774], ...
                     'Position',      [129 76 891 588]);
                 
%         frame = getframe ;
%         ima = frame.cdata(:,:,1);                 % save full image
%         % Gaussian noise
%         ima = min(255,max(0,ima+uint8(sigmaCameraNoise*randn(size(ima)))));
%         figure(hfFrames+1),imshow(ima)
%         pause
        
        if (saveMovie)
            frame = getframe ;
            ima = frame.cdata(:,:,1);                 % save full image
            % Additive Gaussian noise
            % ima = min(255,max(0,ima+uint8(sigmaCameraNoise*randn(size(ima)))));
            % figure(hfFrames+1),imshow(ima)
            % pause
            
%             % pel programa VC++ de deteccio linies de carril
%             imaName = ['Results/Sequence/tif/' num2str(i,'%06u') '.tif'];
%             imwrite(ima, imaName,'Compression','none');
%             gzip(imaName);
%             delete(imaName);
            % also save in compressed JPG to make the video faster with Radtools
            imaName = ['Results\' num2str(i,'%06u') '.jpg'];
            
            imwrite(ima, imaName);
            clear frame
        end
        if (plotTrueLaneLines)
            grid on
            hold on
            plot(leftMostLineCentersFrame(1,:),  leftMostLineCentersFrame(2,:), 'r')
            plot(middleLineCentersFrame(1,:),    middleLineCentersFrame(2,:),   'g')
            plot(rightMostLineCentersFrame(1,:), rightMostLineCentersFrame(2,:),'b')
            plot(u_l, v_l,'ro') 
            plot(u_r, v_r,'ro')
            hold off
        end
        drawnow
        % pause
        clf
    end
end
        
%--------------------------------------------------------
% Ground truth from the synthetic road, limited to 
% those parameters which are later computed by the method
%--------------------------------------------------------
% to avoid last frames, when patches ahead do not fill the 
% image below the horizon line and lane
% points and therefore parameter computations are not reliable
roadLength = roadLength - offsetBorder ; 

roadHeight = roadHeight(1:roadLength);
roadSlope = roadSlope(1:roadLength);
cameraPitch = cameraPitch(1:roadLength);
ld = ld(1:roadLength);
roadCurvature = roadCurvature(1:roadLength);
a1 = atan2(cameraViewingDirection(:,2),cameraViewingDirection(:,1)) ;
a2 = atan2(vn(1:roadLength,2),vn(1:roadLength,1)) ;
gtTheta = (a1-sign(a1)*pi)-(a2-sign(a2)*pi);
ii = find(abs(gtTheta)>pi);
gtTheta(ii) = 2*pi-sign(gtTheta(ii)).*gtTheta(ii);

groundTruth = struct('L',     (laneWidth + borderLaneLineWidth/2.0 + interLaneLineWidth/2.0)*ones(roadLength,1), ...
                     'theta', gtTheta     ,  ...
                     'x_c',   ld          ,  ...
                     'C',     roadCurvature  ...
                    ) ; 

%-------------
% Save results
%-------------      
if (saveResult)
    save(['Results/' nameResult '.mat']) ;
end

