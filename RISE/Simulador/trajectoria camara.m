pLaneChange      = 0.0 ;  % Probability of lane change each time the direction changes
pDirectionChange = 0.05 ; % Probability of direction change per meter
sigmaLateralDisplacement = 0.3 ;
minDistanceBetweenDirectionChanges = 10 ; % meters, to avoid sudden direction change in trajectory
%-------------------------------------
% Computation of the camera trajectory
%-------------------------------------
nDirectionChanges = pDirectionChange*roadLength + 2; % +2 because of begining and end of road points
longitudinalPositionIndex = unique([1 ...
                                    uint32(round(sort(rand(1,nDirectionChanges-2)*roadLength))) ...
                                    roadLength]) ; % in meters, in ascending order and no repeated values
longitudinalPositionIndex = longitudinalPositionIndex( ...
      find(longitudinalPositionIndex(2:end)-longitudinalPositionIndex(1:end-1)>minDistanceBetweenDirectionChanges));
nDirectionChanges = length(longitudinalPositionIndex);
% lateralDisplacement = min(max(0,laneWidth/2 + sqrt(sigmaLateralDisplacement)*randn(1,nDirectionChanges)), ...
%                           laneWidth) ; % with respect the medial axis, single lane
lateralDisplacement = laneWidth/2 + sigmaLateralDisplacement*randn(1,nDirectionChanges) ;
                      
ind = find(rand(1,nDirectionChanges)<pLaneChange);
ind = unique([1 ind nDirectionChanges]);
lane = zeros(1,nDirectionChanges); % 0 = right, 1 = left
for i=2:2:length(ind)-1
    lane(ind(i):ind(i+1)) = 1;
end
lateralDisplacement = lateralDisplacement - laneWidth*lane;                      
ld = pchip(double(longitudinalPositionIndex), lateralDisplacement,[1:roadLength]');
