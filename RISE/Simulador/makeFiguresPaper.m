clear all
close all
disp('makefigurespaper');

%----------------------
% What to plot and save
%----------------------
saveFigures = 1;
plotModelDeviations = 1;
pitchPeaks = [339 738 1185 1457 1888 2381 2590 3114 3647 3929 4230 4608];
slopeChanges = [455 855 1285 1670]; % en falten encara despres dels 2Km
plotModePitchPeaks = '^';
plotModeSlopeChanges = 'o';

%----------------------
% how to plot : axis limits and colors of curves
%----------------------
plotModeCurvatureChanges = 'k:';
plotModeSlopeChanges = 'g:';
plotModePitchPeaks = 'y:';
plotModeComputed = 'b-';
plotModeGT = 'r-';
limitstime = [1 2000]; % limits of horizontal axis
limitsC = [-0.015 0.015]; % limits of vertical axis for each parameter
limitsxc = [-2 4];
limitsL = [2 5];
limitstheta = [-4 4];
amp = 1.3;             % amp = lowFrequencyMaximumAmplitude + highFrequencyMaximumAmplitude;
limitsphi = [0.3 2.9]; % amp = [nominalCameraPitch*180/pi-amp nominalCameraPitch*180/pi+amp];
Position = [5 408 1016 282]; % aspect ratio of figures x_c, C, L, theta and phi
plotModePitchPeaks = '^';
plotModeSlopeChanges = 'o';
markersize = 30; % for model deviations

%----------------------
% input data
%----------------------
shiftCurvature = 16;

%
% Non-ideal road
%
load 'Results/Non-ideal road.mat'
nonIdealRoad = computed ;

%
% Non-ideal camera
%
load 'Results/Non-ideal camera.mat'
nonIdealCamera = computed ;

%
% Non-ideal detection, 1 phi (nominal camera pitch)
%
sc = 14; 
% 14 : 1 \psi = valor nominal
% 16 : 3 \psi
% 13 : 7 \psi
% 15 : 41 \psi
[isSet,xc,C0,theta,L,psi] = textread(sprintf('Results/curveDetectionJoanResults - %d.txt',sc));
nonIdealDetection = struct('xc',[],'C',[],'theta',[],'L',[],'psi',[]);
nonIdealDetection.xc = xc;
nonIdealDetection.C = C0;
nonIdealDetection.theta = theta;
nonIdealDetection.L = L;
nonIdealDetection.psi = psi;


%
% Non-ideal detection, 7 phis
%
sc = 13;  
[isSet,xc,C0,theta,L,psi] = textread(sprintf('Results/curveDetectionJoanResults - %d.txt',sc));
nonIdealDetection7Phis = struct('xc',[],'C',[],'theta',[],'L',[],'psi',[]);
nonIdealDetection7Phis.xc = xc;
nonIdealDetection7Phis.C = C0;
nonIdealDetection7Phis.theta = theta;
nonIdealDetection7Phis.L = L;
nonIdealDetection7Phis.psi = psi;
sc = 15;  
[isSet,xc,C0,theta,L,psi] = textread(sprintf('Results/curveDetectionJoanResults - %d.txt',sc));
nonIdealDetection41Phis = struct('xc',[],'C',[],'theta',[],'L',[],'psi',[]);
nonIdealDetection41Phis.xc = xc;
nonIdealDetection41Phis.C = C0;
nonIdealDetection41Phis.theta = theta;
nonIdealDetection41Phis.L = L;
nonIdealDetection41Phis.psi = psi;

%
% Non-ideal detection several phis + median filtering
%
w = 65 ; % 0 = no smoothing (use original values)
wphi = 5; % window for median filtering of computed phi is narrower


%------------------------------
% Values needed in some figures
%------------------------------
if (straightRoad)
    nSegmentsC = 0 ;
    segmentEndsC = [];
else
    segmentEndsC = cumsum(segmentLengthC) ;
end
if (flatRoad)
    nSegmentsH = 0 ;
    segmentEndsH = [];
else
    segmentEndsH = cumsum(segmentLengthH) ;
end

%----------------------------------------
% Road and camera parameters
%----------------------------------------
% Slope
figure
hfSlope = gcf ;
set(gcf,'Name','Groundtruth slope')
plot(roadSlope,'b')
xlim(limitstime)
xlabel('frame')
ylabel('slope')

% nomimal and ground truth phi
figure
hfPhiGT = gcf ;
set(gcf,'Name','Groundtruth phi','PaperPositionMode','auto')
plot(cameraPitch*180/pi)
hold on
plot([0 roadLength],[nominalCameraPitch*180/pi nominalCameraPitch*180/pi])
hold off
xlim(limitstime)
ylim(limitsphi)
xlabel('frame')
ylabel('camera pitch (degrees)')

%----------------------------------------
% x_c, theta, and C ground truth only (L no, it's constant)
%----------------------------------------
% Curvature
figure
hfCurvatureGT = gcf ;
set(gcf,'Name','Groundtruth C','Position',Position,'PaperPositionMode','auto')
plot(groundTruth.C,'b')
xlim (limitstime)
ylim(limitsC)
xlabel('frame')
ylabel('curvature (1/meter)')

% theta
figure
hfThetaGT = gcf ;
set(gcf,'Name','Groundtruth theta','Position',Position,'PaperPositionMode','auto')
plot(groundTruth.theta*180/pi)
% hold on
xlim (limitstime)
ylim (limitstheta) % degrees
xlabel('frame')
ylabel('\theta (degrees)')
hold off

% x_c
figure
hfxcGT = gcf ;
set(gcf,'Name','Groundtruth xc','Position',Position,'PaperPositionMode','auto')
plot(groundTruth.x_c)
% hold on
xlim (limitstime)
ylim (limitsxc) % degrees
xlabel('frame')
ylabel('x_c')
hold off


%-----------------------------
% 3d Road 
%-----------------------------
% 3d Road, with junctions of curvature and height segments
figure
hfRoad3d = gcf ;
set(gcf,'Name','3D road')
patch('Faces', faces, 'Vertices', vertices, 'FaceVertexCData',colors, ...
      'FaceColor','flat','Linestyle','none');
axis equal
hold on
% draw camera (=vehicle) trajectory
plot3(xCameraTrajectory, yCameraTrajectory, zCameraTrajectory, 'g-')
% % NO CAL PER QUE NO S'ARRIBA A VEURE EN LA FIGURA EN PAPER
% % draw curvature changes positions in red
% for i=1:nSegmentsC
%     cse = segmentEndsC(i) ;
%     plot3([vertices((cse-1)*nVerticesAcross+1,1) vertices(cse*nVerticesAcross,1)] , ...
%           [vertices((cse-1)*nVerticesAcross+1,2) vertices(cse*nVerticesAcross,2)] , ...
%           [vertices((cse-1)*nVerticesAcross+1,3) vertices(cse*nVerticesAcross,3)] , 'r-')
% end
% % draw height changes positions in yellow
% for i=1:nSegmentsH
%     hse = segmentEndsH(i) ;
%     plot3([vertices((hse-1)*nVerticesAcross+1,1) vertices(hse*nVerticesAcross,1)] , ...
%           [vertices((hse-1)*nVerticesAcross+1,2) vertices(hse*nVerticesAcross,2)] , ...
%           [vertices((hse-1)*nVerticesAcross+1,3) vertices(hse*nVerticesAcross,3)] , 'y-')
% end
% title('3d road')
grid on
hold off


%--------------------------------------
% Results for non-ideal road
%--------------------------------------
% theta
figure
hfThetaNIR = gcf ;
set(gcf,'Name','theta Non-ideal road','Position',Position,'PaperPositionMode','auto')
plot(groundTruth.theta*180/pi,plotModeGT)
hold on
plot(nonIdealRoad.theta*180/pi,plotModeComputed)
if(plotModelDeviations)
    plot(slopeChanges,groundTruth.theta(slopeChanges)*180/pi,plotModeSlopeChanges,'markersize',markersize)
end
hold off
xlim(limitstime)
ylim(limitstheta)
xlabel('frame')
ylabel('\theta (degrees)')

% x_c
figure
set(gcf,'Name','xc Non-ideal road','Position',Position,'PaperPositionMode','auto')
hfx_cNIR = gcf ;
plot(groundTruth.x_c,plotModeGT)
hold on
plot(nonIdealRoad.x_c,plotModeComputed)
if(plotModelDeviations)
    plot(slopeChanges,groundTruth.x_c(slopeChanges),plotModeSlopeChanges,'markersize',markersize)
end
hold off
xlim(limitstime)
ylim(limitsxc)
xlabel('frame')
ylabel('x_c (meters)')

% C
figure
set(gcf,'Name','C Non-ideal road','Position',Position,'PaperPositionMode','auto')
hfCNIR = gcf ;
plot(groundTruth.C,plotModeGT)
hold on
plot([shiftCurvature:shiftCurvature+roadLength-1], nonIdealRoad.C,plotModeComputed)
if(plotModelDeviations)
    plot(slopeChanges,groundTruth.C(slopeChanges),plotModeSlopeChanges,'markersize',markersize)
end
hold off
xlim(limitstime)
ylim(limitsC)
xlabel('frame')
ylabel('C')

% L
figure
set(gcf,'Name','L Non-ideal road','Position',Position,'PaperPositionMode','auto')
hfLNIR = gcf ;
plot(groundTruth.L,plotModeGT)
hold on
plot(nonIdealRoad.L,plotModeComputed)
if(plotModelDeviations)
    plot(slopeChanges,groundTruth.L(slopeChanges),plotModeSlopeChanges,'markersize',markersize)
end
hold off
xlim(limitstime)
ylim(limitsL)
xlabel('frame')
ylabel('L (meters)')


%--------------------------------------
% Results for non-ideal camera
%--------------------------------------
% theta
figure
set(gcf,'Name','theta Non-ideal camera','Position',Position,'PaperPositionMode','auto')
hfThetaNIC = gcf ;
plot(groundTruth.theta*180/pi,plotModeGT)
hold on
plot(nonIdealCamera.theta*180/pi,plotModeComputed)
if(plotModelDeviations)
    plot(pitchPeaks,groundTruth.theta(pitchPeaks)*180/pi,plotModePitchPeaks,'markersize',markersize)
    %plot(slopeChanges,groundTruth.theta(slopeChanges)*180/pi,plotModeSlopeChanges,'markersize',markersize)
end
hold off
xlim(limitstime)
ylim(limitstheta)
xlabel('frame')
ylabel('\theta (degrees)')

% x_c
figure
set(gcf,'Name','xc Non-ideal camera','Position',Position,'PaperPositionMode','auto')
hfx_cNIC = gcf ;
plot(groundTruth.x_c,plotModeGT)
hold on
plot(nonIdealCamera.x_c,plotModeComputed)
if(plotModelDeviations)
    plot(pitchPeaks,groundTruth.x_c(pitchPeaks),plotModePitchPeaks,'markersize',markersize)
    %plot(slopeChanges,groundTruth.x_c(slopeChanges),plotModeSlopeChanges,'markersize',markersize)
end
hold off
xlim(limitstime)
ylim(limitsxc)
xlabel('frame')
ylabel('x_c (meters)')

% C
figure
set(gcf,'Name','C Non-ideal camera','Position',Position,'PaperPositionMode','auto')
hfCNIC = gcf ;
plot(groundTruth.C,plotModeGT)
hold on
plot([shiftCurvature:shiftCurvature+roadLength-1], nonIdealCamera.C,plotModeComputed)
if(plotModelDeviations)
    plot(pitchPeaks,groundTruth.C(pitchPeaks),plotModePitchPeaks,'markersize',markersize)
    %plot(slopeChanges,groundTruth.C(slopeChanges),plotModeSlopeChanges,'markersize',markersize)
end
hold off
xlim(limitstime)
ylim(limitsC)
xlabel('frame')
ylabel('C')

% L
figure
set(gcf,'Name','L Non-ideal camera','Position',Position,'PaperPositionMode','auto')
hfLNIC = gcf ;
plot(groundTruth.L,plotModeGT)
hold on
plot(nonIdealCamera.L,plotModeComputed)
if(plotModelDeviations)
    plot(pitchPeaks,groundTruth.L(pitchPeaks),plotModePitchPeaks,'markersize',markersize)
    %plot(slopeChanges,groundTruth.L(slopeChanges),plotModeSlopeChanges,'markersize',markersize)
end
hold off
xlim(limitstime)
ylim(limitsL)
xlabel('frame')
ylabel('L (meters)')

%------------------------------------------------------
% Results for non-ideal detection, 1 phi (nominal pitch)
%------------------------------------------------------
% theta
figure
set(gcf,'Name','theta Non-ideal detection','Position',Position,'PaperPositionMode','auto')
hfThetaNID = gcf ;
plot(groundTruth.theta*180/pi,plotModeGT)
hold on
plot(nonIdealDetection.theta*180/pi,plotModeComputed)
hold off
xlim(limitstime)
ylim(limitstheta)
xlabel('frame')
ylabel('\theta (degrees)')

% x_c
figure
set(gcf,'Name','xc Non-ideal detection','Position',Position,'PaperPositionMode','auto')
hfx_cNID = gcf ;
plot(groundTruth.x_c,plotModeGT)
hold on
plot(nonIdealDetection.xc,plotModeComputed)
hold off
xlim(limitstime)
ylim(limitsxc)
xlabel('frame')
ylabel('x_c (meters)')

% C
figure
set(gcf,'Name','C Non-ideal detection','Position',Position,'PaperPositionMode','auto')
hfCNID = gcf ;
plot(groundTruth.C,plotModeGT)
hold on
plot([shiftCurvature:shiftCurvature+roadLength-1], nonIdealDetection.C,plotModeComputed)
hold off
xlim(limitstime)
ylim(limitsC)
xlabel('frame')
ylabel('C')

% L
figure
set(gcf,'Name','L Non-ideal detection','Position',Position,'PaperPositionMode','auto')
hfLNID = gcf ;
plot(groundTruth.L,plotModeGT)
hold on
plot(nonIdealDetection.L,plotModeComputed)
hold off
xlim(limitstime)
ylim(limitsL)
xlabel('frame')
ylabel('L (meters)')

% real and computed phi
figure
set(gcf,'Name','computed Phi non-ideal detection','Position',Position,'PaperPositionMode','auto')
hfPhiNID = gcf ;
plot(nonIdealDetection.psi*180/pi,plotModeComputed)
hold on
plot(cameraPitch*180/pi,plotModeGT)
hold off
xlim(limitstime)
ylim(limitsphi)
xlabel('frame')
ylabel('\psi')


%------------------------------------------------------
% Results for non-ideal detection, 7 phis
%------------------------------------------------------
% theta
figure
set(gcf,'Name','theta Non-ideal detection 7 phis','Position',Position,'PaperPositionMode','auto')
hfThetaNID7Phis = gcf ;
plot(groundTruth.theta*180/pi,plotModeGT)
hold on
plot(nonIdealDetection7Phis.theta*180/pi,plotModeComputed)
hold off
xlim(limitstime)
ylim(limitstheta)
xlabel('frame')
ylabel('\theta (degrees)')

% x_c
figure
set(gcf,'Name','xc Non-ideal detection 7 phis','Position',Position,'PaperPositionMode','auto')
hfx_cNID7Phis = gcf ;
plot(groundTruth.x_c,plotModeGT)
hold on
plot(nonIdealDetection7Phis.xc,plotModeComputed)
hold off
xlim(limitstime)
ylim(limitsxc)
xlabel('frame')
ylabel('x_c (meters)')

% C
figure
set(gcf,'Name','C Non-ideal detection 7 phis','Position',Position,'PaperPositionMode','auto')
hfCNID7Phis = gcf ;
plot(groundTruth.C,plotModeGT)
hold on
plot([shiftCurvature:shiftCurvature+roadLength-1], nonIdealDetection7Phis.C,plotModeComputed)
hold off
xlim(limitstime)
ylim(limitsC)
xlabel('frame')
ylabel('C')

% L
figure
set(gcf,'Name','L Non-ideal detection 7 phis','Position',Position,'PaperPositionMode','auto')
hfLNID7Phis = gcf ;
plot(groundTruth.L,plotModeGT)
hold on
plot(nonIdealDetection7Phis.L,plotModeComputed)
hold off
xlim(limitstime)
ylim(limitsL)
xlabel('frame')
ylabel('L (meters)')

% real and computed phi, 7 phis
figure
set(gcf,'Name','computed Phi non-ideal detection 7 phis','Position',Position,'PaperPositionMode','auto')
hfPhiNID7Phis = gcf ;
plot(nonIdealDetection7Phis.psi*180/pi,plotModeComputed)
hold on
plot(cameraPitch*180/pi,plotModeGT)
hold off
xlim(limitstime)
ylim(limitsphi)
xlabel('frame')
ylabel('\psi')

%------------------------------------------------------
% Results for non-ideal detection, 41 phis (only phi)
%------------------------------------------------------
figure
set(gcf,'Name','computed Phi non-ideal detection 41 phis','Position',Position,'PaperPositionMode','auto')
hfPhiNID41Phis = gcf ;
plot(nonIdealDetection41Phis.psi*180/pi,plotModeComputed)
hold on
plot(cameraPitch*180/pi,plotModeGT)
hold off
xlim(limitstime)
ylim(limitsphi)
xlabel('frame')
ylabel('\psi')


%------------------------------------------------------
% Results for non-ideal detection 7 phis, median filtered 
%------------------------------------------------------
% median filter of all points

% theta
figure
set(gcf,'Name','theta Non-ideal detection 7 phis + median','Position',Position,'PaperPositionMode','auto')
hfThetaNID7Phismed = gcf ;
plot(groundTruth.theta*180/pi,plotModeGT)
hold on
plot(med(nonIdealDetection7Phis.theta,w)*180/pi,plotModeComputed)
hold off
xlim(limitstime)
ylim(limitstheta)
xlabel('frame')
ylabel('\theta (degrees)')

% x_c
figure
set(gcf,'Name','xc Non-ideal detection 7 phis + median','Position',Position,'PaperPositionMode','auto')
hfx_cNID7Phismed = gcf ;
plot(groundTruth.x_c,plotModeGT)
hold on
plot(med(nonIdealDetection7Phis.xc,w),plotModeComputed)
hold off
xlim(limitstime)
ylim(limitsxc)
xlabel('frame')
ylabel('x_c (meters)')

% C
figure
set(gcf,'Name','C Non-ideal detection 7 phis + median','Position',Position,'PaperPositionMode','auto')
hfCNID7Phismed = gcf ;
plot(groundTruth.C,plotModeGT)
hold on
plot([shiftCurvature:shiftCurvature+roadLength-1], med(nonIdealDetection7Phis.C,w),plotModeComputed)
hold off
xlim(limitstime)
ylim(limitsC)
xlabel('frame')
ylabel('C')

% L
figure
set(gcf,'Name','L Non-ideal detection 7 phis + median','Position',Position,'PaperPositionMode','auto')
hfLNID7Phismed = gcf ;
plot(groundTruth.L,plotModeGT)
hold on
plot(med(nonIdealDetection7Phis.L,w),plotModeComputed)
hold off
xlim(limitstime)
ylim(limitsL)
xlabel('frame')
ylabel('L (meters)')

% real and computed phi
figure
set(gcf,'Name','computed Phi non-ideal detection 7 phis + median','Position',Position,'PaperPositionMode','auto')
hfPhiNID7Phismed = gcf ;
plot(med(nonIdealDetection7Phis.psi,wphi)*180/pi,plotModeComputed)
hold on
plot(cameraPitch*180/pi,plotModeGT)
hold off
xlim(limitstime)
ylim(limitsphi)
xlabel('frame')
ylabel('\psi')

%------------------------------------------------------
% Results for non-ideal detection 41 phis, median filtered (only phi)
%------------------------------------------------------
figure
set(gcf,'Name','computed Phi non-ideal detection 41 phis + median','Position',Position,'PaperPositionMode','auto')
hfPhiNID41Phismed = gcf ;
plot(med(nonIdealDetection41Phis.psi,wphi)*180/pi,plotModeComputed)
hold on
plot(cameraPitch*180/pi,plotModeGT)
hold off
xlim(limitstime)
ylim(limitsphi)
xlabel('frame')
ylabel('\psi')


%-------------------------------------
% error
%-------------------------------------
scenarios = [14 16 13 15];
% 14 : 1 \psi = valor nominal
% 16 : 3 \psi
% 13 : 7 \psi
% 15 : 41 \psi
err = struct('scenario',[],...
             'xc',[],...
             'C',[],...
             'theta',[],...
             'L',[],...
             'psi',[],...
             'xcmed',[],...
             'Cmed',[],...
             'thetamed',[],...
             'Lmed',[], ...
             'psimed',[]);
for ns=1:length(scenarios)
    sc = scenarios(ns);
    [isSet,xc,C,theta,L,psi] = textread(sprintf('Results/curveDetectionJoanResults - %d.txt',sc));
    
    % median filter of all points
    xcmed = med(xc,w);
    Cmed = med(C,w);
    thetamed = med(theta,w);
    Lmed = med(L,w);
    psimed = med(psi,wphi);

    err(ns).scenario = sc;    
    err(ns).xc = rmse(xc,groundTruth.x_c);
    err(ns).C = rmse(C,groundTruth.C);
    err(ns).theta = rmse(theta*180/pi,groundTruth.theta*180/pi); % error in degrees
    err(ns).L = rmse(L,groundTruth.L);
    err(ns).psi = rmse(psi*180/pi,cameraPitch*180/pi); % error in degrees
        
    err(ns).xcmed = rmse(xcmed,groundTruth.x_c);
    err(ns).Cmed = rmse(Cmed,groundTruth.C);
    err(ns).thetamed = rmse(thetamed*180/pi,groundTruth.theta*180/pi);
    err(ns).Lmed = rmse(Lmed,groundTruth.L);
    err(ns).psimed = rmse(psimed*180/pi,cameraPitch*180/pi);
end

hfErrxc=figure;
bar([[err(:).xc]'    [err(:).xcmed]'],   'group'), title('x_c') 
% ylabel('RMSE')
a=gca;set(a,'XTickLabel',[{'1'}; {'3'} ;{'7'} ;{'41'}])
hfErrC = figure;
bar([[err(:).C]'     [err(:).Cmed]'],   'group'), title('C_0')
% ylabel('RMSE')
a=gca;set(a,'XTickLabel',[{'1'}; {'3'} ;{'7'} ;{'41'}])
hfErrtheta = figure;
bar([[err(:).theta]' [err(:).thetamed]'],'group'), title('\theta') 
% ylabel('RMSE')
a=gca;set(a,'XTickLabel',[{'1'}; {'3'} ;{'7'} ;{'41'}])
hfErrL = figure;
bar([[err(:).L]'     [err(:).Lmed]'],    'group'), title('L') 
% ylabel('RMSE')
a=gca;set(a,'XTickLabel',[{'1'}; {'3'} ;{'7'} ;{'41'}])
hfErrpsi = figure;
bar([[err(:).psi]'   [err(:).psimed]'],  'group'), title('\psi') 
% ylabel('RMSE')
a=gca;set(a,'XTickLabel',[{'1'}; {'3'} ;{'7'} ;{'41'}])

%--------------------------------------
% Save figures in EPS
%--------------------------------------
if (saveFigures)
    pathFigures = '../Latex/Robust lane markings detection/Figures/';
    % road and camera parameters
    print(hfSlope,        '-depsc', [pathFigures 'slope.eps']      )
    print(hfPhiGT,        '-depsc', [pathFigures 'phi.eps']        )
    % ground truth
    print(hfxcGT,         '-depsc', [pathFigures 'xc.eps']  )
    print(hfCurvatureGT,  '-depsc', [pathFigures 'curvature.eps']  )
    print(hfThetaGT,      '-depsc', [pathFigures 'theta.eps']      )
    % 3d road and sample frame
    print(hfRoad3d,       '-depsc', [pathFigures 'road3d.eps']     )    
    % non-ideal road
    print(hfThetaNIR,     '-depsc', [pathFigures 'thetaNIR.eps']   )
    print(hfx_cNIR,       '-depsc', [pathFigures 'x_cNIR.eps']     )
    print(hfCNIR,         '-depsc', [pathFigures 'CNIR.eps']       )
    print(hfLNIR,         '-depsc', [pathFigures 'LNIR.eps']       )
    % non-ideal camera
    print(hfThetaNIC,     '-depsc', [pathFigures 'thetaNIC.eps']   )
    print(hfx_cNIC,       '-depsc', [pathFigures 'x_cNIC.eps']     )
    print(hfCNIC,         '-depsc', [pathFigures 'CNIC.eps']       )
    print(hfLNIC,         '-depsc', [pathFigures 'LNIC.eps']       )
    % non-ideal detection, nominal pitch (1 phi)
    print(hfThetaNID,     '-depsc', [pathFigures 'thetaNID.eps']   )
    print(hfx_cNID,       '-depsc', [pathFigures 'x_cNID.eps']     )
    print(hfCNID,         '-depsc', [pathFigures 'CNID.eps']       )
    print(hfLNID,         '-depsc', [pathFigures 'LNID.eps']       )
    print(hfPhiNID,       '-depsc', [pathFigures 'PhiNID.eps']     )
    % non-ideal detection, 7 phis
    print(hfThetaNID7Phis,     '-depsc', [pathFigures 'thetaNID7phis.eps']   )
    print(hfx_cNID7Phis,       '-depsc', [pathFigures 'x_cNID7phis.eps']     )
    print(hfCNID7Phis,         '-depsc', [pathFigures 'CNID7phis.eps']       )
    print(hfLNID7Phis,         '-depsc', [pathFigures 'LNID7phis.eps']       )
    print(hfPhiNID7Phis,       '-depsc', [pathFigures 'PhiNID7phis.eps']     )
    % non-ideal detection, 41 phis
    print(hfPhiNID41Phis,       '-depsc', [pathFigures 'PhiNID41phis.eps']     )
    % non-ideal detection, 7 phis + median filtering 
    print(hfThetaNID7Phismed,     '-depsc', [pathFigures 'thetaNID7Phismed.eps']   )
    print(hfx_cNID7Phismed,       '-depsc', [pathFigures 'x_cNID7Phismed.eps']     )
    print(hfCNID7Phismed,         '-depsc', [pathFigures 'CNID7Phismed.eps']       )
    print(hfLNID7Phismed,         '-depsc', [pathFigures 'LNID7Phismed.eps']       )
    print(hfPhiNID7Phismed,       '-depsc', [pathFigures 'PhiNID7Phismed.eps']     )
    % non-ideal detection, 41 phis + median filtering 
    print(hfPhiNID41Phismed,       '-depsc', [pathFigures 'PhiNID41Phismed.eps']     )    
    % RMSE
    print(hfErrxc,      '-depsc', [pathFigures 'errxc.eps']      )
    print(hfErrC,       '-depsc', [pathFigures 'errC.eps']       )
    print(hfErrtheta,   '-depsc', [pathFigures 'errtheta.eps']   )
    print(hfErrL,       '-depsc', [pathFigures 'errL.eps']       )
    print(hfErrpsi,     '-depsc', [pathFigures 'errpsi.eps']     )
    % uphill sequence and sample frame
    dirjpgs = dir('Results/*.jpg');
    for i=1:length(dirjpgs)
        name = dirjpgs(i).name;
        ima = imread(['Results/' name]);
        ima = ima(:,8:end,1); % 8 to avoid tick marks on the left vertical axis
        ima(1,1:end)=0; ima(end,1:end)=0; ima(1:end,1)=0; ima(1:end,end)=0; % frame
        hfFrame = figure;
        imshow(ima)
        print(hfFrame, '-depsc', [pathFigures name(1:strfind(name,'.')-1) '.eps'])
    end
    
end
