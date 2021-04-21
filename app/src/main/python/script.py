import numpy as np
import cv2
import imutils
from matplotlib.path import Path
from colormath.color_objects import sRGBColor, LabColor
from colormath.color_conversions import convert_color
import face_recognition
import base64

dry_lips_open_mouth_diff_mean = 178.85595374253367
dry_lips_open_mouth_diff_dev = 97.66810118612692
dry_lips_open_mean = 2.239130434782609
dry_lips_open_dev = 3.311277059624499


def get_rgb(photo):
    rgb_r = 0
    rgb_g = 0
    rgb_b = 0
    for i in range(photo.shape[0]):
        for j in range(photo.shape[1]):
            pixel = photo[i][j]
            a1, a2, a3 = pixel / 255
            rgb = sRGBColor(a1, a2, a3)
            rgb_r += rgb.rgb_r
            rgb_b += rgb.rgb_b
            rgb_g += rgb.rgb_g

    size = photo.shape[0] * photo.shape[1]

    return rgb_r / size, rgb_g / size, rgb_b / size


def crop_image(part, image):
    vertices = part

    img = imutils.resize(image, width=500)

    # from vertices to a matplotlib path
    path = Path(vertices)

    # create a mesh grid for the whole image, you could also limit the
    # grid to the extents above, I'm creating a full grid for the plot below
    x, y = np.mgrid[:img.shape[1], :img.shape[0]]
    # mesh grid to a list of points
    points = np.vstack((x.ravel(), y.ravel())).T

    # select points included in the path
    mask = path.contains_points(points)

    # reshape mask for display
    img_mask = mask.reshape(x.shape).T

    # masked image
    img *= img_mask[..., None]
    return img


def calculate_a_sum(photo):
    a = []
    for i in range(photo.shape[0]):
        for j in range(photo.shape[1]):
            pixel = photo[i][j]
            a1, a2, a3 = pixel / 255
            rgb = sRGBColor(a1, a2, a3)
            if rgb.rgb_r != 0 or rgb.rgb_g != 0 or rgb.rgb_b != 0:
                a.append(rgb_to_cielab(pixel).lab_a)

    av_a = sum(a)
    return av_a, len(a)


def calculate_a_b_sum(photo):
    a = []
    b = []
    for i in range(photo.shape[0]):
        for j in range(photo.shape[1]):
            pixel = photo[i][j]
            a1, a2, a3 = pixel / 255
            rgb = sRGBColor(a1, a2, a3)
            if rgb.rgb_r != 0 or rgb.rgb_g != 0 or rgb.rgb_b != 0:
                a.append(rgb_to_cielab(pixel).lab_a)
                b.append(rgb_to_cielab(pixel).lab_b)

    av_a = sum(a)
    av_b = sum(b)

    return av_a, av_b, len(a)


def rgb_to_cielab(a):
    # a is a pixel with RGB coloring
    a1, a2, a3 = a / 255

    color1_rgb = sRGBColor(a1, a2, a3)

    color1_lab = convert_color(color1_rgb, LabColor)

    return color1_lab


def highest_point(arr):
    arr_y = []
    for point in arr:
        arr_y.append(point[1])
    return min(arr_y)


def check_zero(edge):
    for item in edge:
        if item != 0:
            return False
    return True


def calc_not_zero(edges):
    not_zero = 0
    for edge in edges:
        for item in edge:
            if item != 0:
                not_zero += 1
    return not_zero


def detect_color(bounds, img):
    # loop over the boundaries
    for (lower, upper) in bounds:
        # create NumPy arrays from the boundaries
        lower = np.array(lower, dtype="uint8")
        upper = np.array(upper, dtype="uint8")

        # find the colors within the specified boundaries and apply
        # the mask
        mask = cv2.inRange(img, lower, upper)

        return calc_not_zero(mask)


def get_color_range(photo):
    red = []
    green = []
    blue = []

    for i in range(photo.shape[0]):
        for j in range(photo.shape[1]):
            pixel = photo[i][j]
            red.append(pixel[0])
            green.append(pixel[1])
            blue.append(pixel[2])

    mean_red, dev_red = calc(red)
    mean_green, dev_green = calc(green)
    mean_blue, dev_blue = calc(blue)

    boundaries = [
        ([mean_red - dev_red, mean_green - dev_green, mean_blue - dev_blue], [mean_red + dev_red, mean_green + dev_green, mean_blue + dev_blue])
    ]

    return boundaries


def calc(arr):
    mean = sum(arr) / len(arr)
    dispersion = np.sqrt(sum([(xi - mean)**2 for xi in arr]) / len(arr))

    return mean, dispersion


def cross_out_feature(part, image):
    vertices = part

    img = imutils.resize(image, width=500)

    # from vertices to a matplotlib path
    path = Path(vertices)

    # create a mesh grid for the whole image, you could also limit the
    # grid to the extents above, I'm creating a full grid for the plot below
    x, y = np.mgrid[:img.shape[1], :img.shape[0]]
    # mesh grid to a list of points
    points = np.vstack((x.ravel(), y.ravel())).T

    # select points included in the path
    mask = path.contains_points(points)

    for i in range(len(mask)):
        if mask[i]:
            mask[i] = False
        else:
            mask[i] = True

    # reshape mask for display
    img_mask = mask.reshape(x.shape).T

    # masked image
    img *= img_mask[..., None]
    return img


def eye_bags_detection(data):
    decoded_data = base64.b64decode(data)
    np_data = np.fromstring(decoded_data, np.uint8)
    image = cv2.imdecode(np_data, cv2.IMREAD_UNCHANGED)

    try:
        image = imutils.resize(image, width=500)
        face_landmarks_list = face_recognition.face_landmarks(image)
        face_landmarks_dict = face_landmarks_list[0]
    except:
        return 0, 0, 0
    else:
        left_eye = face_landmarks_dict.get('left_eye')
        right_eye = face_landmarks_dict.get('right_eye')
        (x, y, w, h) = cv2.boundingRect(np.array([left_eye]))
        coeff = 5
        roi = image[y + h + coeff:y + 2 * h + coeff, x:x + w]
        red_right_eye, green_right_eye, blue_right_eye = get_rgb(roi)
        roi_skin = image[y + 2 * h + coeff:y + 4 * h + coeff, x:x + w]
        red_skin_right_eye, green_skin_right_eye, blue_skin_right_eye = get_rgb(roi_skin)

        (x, y, w, h) = cv2.boundingRect(np.array([right_eye]))
        coeff = 5
        roi = image[y + h + coeff:y + 2 * h + coeff, x:x + w]
        red_left_eye, green_left_eye, blue_left_eye = get_rgb(roi)
        roi_skin = image[y + 2 * h + coeff:y + 4 * h + coeff, x:x + w]
        red_skin_left_eye, green_skin_left_eye, blue_skin_left_eye = get_rgb(roi_skin)

        red_diff_right = abs(red_right_eye - red_skin_right_eye)
        red_diff_left = abs(red_left_eye - red_skin_left_eye)
        green_diff_right = abs(green_right_eye - green_skin_right_eye)
        green_diff_left = abs(green_left_eye - green_skin_left_eye)
        blue_diff_right = abs(blue_right_eye - blue_skin_right_eye)
        blue_diff_left = abs(blue_left_eye - blue_skin_left_eye)

        red_diff = red_diff_right + red_diff_left
        green_diff = green_diff_right + green_diff_left
        blue_diff = blue_diff_right + blue_diff_left

        return red_diff, green_diff, blue_diff


def blue_lips_detection(data):
    decoded_data = base64.b64decode(data)
    np_data = np.fromstring(decoded_data, np.uint8)
    image = cv2.imdecode(np_data, cv2.IMREAD_UNCHANGED)

    try:
        image = imutils.resize(image, width=500)
        face_landmarks_list = face_recognition.face_landmarks(image)
        face_landmarks_dict = face_landmarks_list[0]
    except:
        return 0
    else:
        bottom_lip = face_landmarks_dict.get('bottom_lip')
        top_lip = face_landmarks_dict.get('top_lip')
        lips = bottom_lip + top_lip
        img = crop_image(lips, image)
        a_lip, sum = calculate_a_sum(img)
        return a_lip / sum


def red_eyes_detection(data):
    decoded_data = base64.b64decode(data)
    np_data = np.fromstring(decoded_data, np.uint8)
    image = cv2.imdecode(np_data, cv2.IMREAD_UNCHANGED)

    try:
        image = imutils.resize(image, width=500)
        face_landmarks_list = face_recognition.face_landmarks(image)
        face_landmarks_dict = face_landmarks_list[0]
    except:
        return 0, 0
    else:
        left_eye = face_landmarks_dict.get('left_eye')
        right_eye = face_landmarks_dict.get('right_eye')
        img = crop_image(left_eye, image)
        a_right_eye, sum_right_eye = calculate_a_sum(img)

        img = crop_image(right_eye, image)
        a_left_eye, sum_left_eye = calculate_a_sum(img)
        a_right = a_right_eye / sum_right_eye
        a_left = a_left_eye / sum_left_eye

        return a_left, a_right


def asymmetric_face_detection(data):
    decoded_data = base64.b64decode(data)
    np_data = np.fromstring(decoded_data, np.uint8)
    image = cv2.imdecode(np_data, cv2.IMREAD_UNCHANGED)

    try:
        image = imutils.resize(image, width=500)
        face_landmarks_list = face_recognition.face_landmarks(image)
        face_landmarks_dict = face_landmarks_list[0]
    except:
        return 0, 0, 0
    else:
        left_eye = face_landmarks_dict.get('left_eye')
        right_eye = face_landmarks_dict.get('right_eye')
        left_eye_center = np.asarray(left_eye).mean(axis=0).astype("int")
        right_eye_center = np.asarray(right_eye).mean(axis=0).astype("int")
        dY = right_eye_center[1] - left_eye_center[1]
        dX = right_eye_center[0] - left_eye_center[0]
        angle_eyes = np.degrees(np.arctan2(dY, dX)) - 180

        left_eyebrow = face_landmarks_dict.get('left_eyebrow')
        right_eyebrow = face_landmarks_dict.get('right_eyebrow')
        max_left_eyebrow = highest_point(left_eyebrow)
        max_right_eyebrow = highest_point(right_eyebrow)

        eyes = abs(180 - abs(angle_eyes))

        bottom_lip = face_landmarks_dict.get('bottom_lip')
        top_lip = face_landmarks_dict.get('top_lip')
        mouth = abs(top_lip[11][1] - bottom_lip[0][1])

        eyebrows = abs(max_left_eyebrow - max_right_eyebrow)

        return eyes, eyebrows, mouth


def depression_detection(data):
    decoded_data = base64.b64decode(data)
    np_data = np.fromstring(decoded_data, np.uint8)
    image = cv2.imdecode(np_data, cv2.IMREAD_UNCHANGED)

    try:
        image = imutils.resize(image, width=500)
        face_landmarks_list = face_recognition.face_landmarks(image)
        face_landmarks_dict = face_landmarks_list[0]
    except:
        return 0
    else:
        bottom_lip = face_landmarks_dict.get('bottom_lip')
        top_lip = face_landmarks_dict.get('top_lip')
        mouth = bottom_lip + top_lip
        mouth_left_edge = bottom_lip[0][1]
        mouth_right_edge = top_lip[11][1]
        mouth_center = np.asarray(mouth).mean(axis=0).astype("int")
        mouthh = (mouth_center[1] - mouth_left_edge + mouth_center[1] - mouth_right_edge) / 2
        return mouthh


def dry_lips_detection(data):
    decoded_data = base64.b64decode(data)
    np_data = np.fromstring(decoded_data, np.uint8)
    image = cv2.imdecode(np_data, cv2.IMREAD_UNCHANGED)

    try:
        image = imutils.resize(image, width=500)
        face_landmarks_list = face_recognition.face_landmarks(image)
        face_landmarks_dict = face_landmarks_list[0]
    except:
        return 0
    else:
        bottom_lip = face_landmarks_dict.get('bottom_lip')
        top_lip = face_landmarks_dict.get('top_lip')
        mouth = bottom_lip + top_lip
        mouth_down =bottom_lip[9][1]
        mouth_up = top_lip[9][1]
        img = crop_image(mouth, image)
        edges = cv2.Canny(img, 100, 200)
        mouth_diff = mouth_down - mouth_up
        lips = calc_not_zero(edges)

        if mouth_diff > dry_lips_open_mean + 2 * dry_lips_open_dev:
            lips -= dry_lips_open_mouth_diff_mean
            lips -= 0.5 * dry_lips_open_mouth_diff_dev
        return lips


def skin_color_detection(data):
    decoded_data = base64.b64decode(data)
    np_data = np.fromstring(decoded_data, np.uint8)
    image = cv2.imdecode(np_data, cv2.IMREAD_UNCHANGED)

    try:
        image = imutils.resize(image, width=500)
        face_landmarks_list = face_recognition.face_landmarks(image)
        face_landmarks_dict = face_landmarks_list[0]
    except:
        return 0
    else:
        left_eyebrow = face_landmarks_dict.get('left_eyebrow')
        right_eyebrow = face_landmarks_dict.get('right_eyebrow')

        coeff = 25
        roi = image[left_eyebrow[2][1] - coeff * 2:left_eyebrow[2][1] - 2, left_eyebrow[2][0]:right_eyebrow[2][0]]

        a_forehead, sum = calculate_a_sum(roi)
        a_forehead /= sum

        left_eye = face_landmarks_dict.get('left_eye')
        right_eye = face_landmarks_dict.get('right_eye')
        (x, y, w, h) = cv2.boundingRect(np.asarray(left_eye))
        coeff = 5
        roi_skin_left = image[y + 2 * h + coeff:y + 4 * h + coeff, x:x + w]

        a_left_skin, sum = calculate_a_sum(roi_skin_left)
        a_left_skin /= sum

        (x, y, w, h) = cv2.boundingRect(np.asarray(right_eye))
        coeff = 5
        roi_skin_right = image[y + 2 * h + coeff:y + 4 * h + coeff, x:x + w]

        a_right_skin, sum = calculate_a_sum(roi_skin_right)
        a_right_skin /= sum

        return (a_forehead + a_left_skin + a_right_skin) / 3


def eyebrows_alopecia_detection(data):
    decoded_data = base64.b64decode(data)
    np_data = np.fromstring(decoded_data, np.uint8)
    image = cv2.imdecode(np_data, cv2.IMREAD_UNCHANGED)

    try:
        image = imutils.resize(image, width=500)
        face_landmarks_list = face_recognition.face_landmarks(image)
        face_landmarks_dict = face_landmarks_list[0]
    except:
        return 0
    else:
        left_eyebrow = face_landmarks_dict.get('left_eyebrow')
        right_eyebrow = face_landmarks_dict.get('right_eyebrow')

        coeff = 25
        roi = image[left_eyebrow[2][1] - coeff * 2:left_eyebrow[2][1] - 2, left_eyebrow[2][0]:right_eyebrow[2][0]]
        bounds = get_color_range(roi)

        img = crop_image(left_eyebrow, image)
        not_zero = detect_color(bounds, img)
        img = crop_image(right_eyebrow, image)
        not_zero += detect_color(bounds, img)
        return not_zero


def redness_detection(data):
    decoded_data = base64.b64decode(data)
    np_data = np.fromstring(decoded_data, np.uint8)
    image = cv2.imdecode(np_data, cv2.IMREAD_UNCHANGED)

    try:
        image = imutils.resize(image, width=500)
        face_location = face_recognition.face_locations(image)
        top, right, bottom, left = face_location[0]
        roi = image[top:bottom, left:right]
        roi = imutils.resize(roi, width=500)

        face_landmarks_list = face_recognition.face_landmarks(roi)
        face_landmarks_dict = face_landmarks_list[0]
    except:
        return 0
    else:
        bottom_lip = face_landmarks_dict.get('bottom_lip')
        top_lip = face_landmarks_dict.get('top_lip')
        lips = bottom_lip + top_lip

        img = cross_out_feature(lips, roi)
        bounds = [
            ([106, 93, 175], [160, 140, 230])
        ]
        not_zero = detect_color(bounds, img)
        return not_zero


# if __name__ == "__main__":
#     image = cv2.imread("Data/1 (1).jpg")
#     image = imutils.resize(image, width=500)
#     print(detection(image))

