a = axis;
disp('plotchangesplaces');

if (showCurvatureChanges)
    for i=1:length(segmentEndsC)
        plot([segmentEndsC(i) segmentEndsC(i)],[a(3) a(4)],plotModeCurvatureChanges)
    end
end
if (showSlopeChanges)
    for i=1:length(segmentEndsH)
        plot([segmentEndsH(i) segmentEndsH(i)],limitsC,plotModeSlopeChanges)
    end
end

a = axis;
if (showCurvatureChanges)
    for i=1:length(segmentEndsC), plot([segmentEndsC(i) segmentEndsC(i)],[a(3) a(4)],plotModeCurvatureChanges), end
end
if (showSlopeChanges)
    for i=1:length(segmentEndsH), plot([segmentEndsH(i) segmentEndsH(i)],[a(3) a(4)],plotModeSlopeChanges), end
end
if (showPitchPeaks)
    for i=1:length(pitchPeaks), plot([pitchPeaks(i) pitchPeaks(i)],[a(3) a(4)],plotModePitchPeaks), end
end
